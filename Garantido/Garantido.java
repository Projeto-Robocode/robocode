package Garantido;

import robocode.*;
import robocode.util.Utils;
import java.awt.Color;
import java.util.HashMap;
import java.util.Map;

public class Garantido extends AdvancedRobot {

    private static final double GRAVITY_FORCE = 5000; // Força de repulsão ajustada
    private static final double WALL_MARGIN = 50; // Margem de distância da parede
    private static final double MAX_FORCE = 8000; // Limite maior para força total
    private static final double MAX_BULLET_POWER = 3; // Potência máxima de tiro
    private static final double MIN_BULLET_POWER = 1; // Potência mínima de tiro

    private ScannedRobotEvent lastScannedRobot;
    private Map<String, EnemyData> enemyDataMap = new HashMap<>(); // Armazena dados sobre os inimigos

    @Override
    public void run() {
        // Configurações iniciais
        setColors(Color.BLUE, Color.WHITE, Color.BLACK); // Corpo, arma, radar
        setAdjustGunForRobotTurn(true);
        setAdjustRadarForGunTurn(true);

        while (true) {
            if (lastScannedRobot != null) {
                doAntiGravityMovement();
                aimAndFire(lastScannedRobot); // Mira e atira no inimigo
            } else {
                // Se nenhum robô foi detectado, mova-se aleatoriamente
                setTurnRight(20);
                setAhead(100);
            }
            setTurnRadarRight(Double.POSITIVE_INFINITY); // Manter o radar girando
            execute();
        }
    }

    @Override
    public void onScannedRobot(ScannedRobotEvent e) {
        lastScannedRobot = e;

        // Atualiza os dados do inimigo
        String enemyName = e.getName();
        enemyDataMap.putIfAbsent(enemyName, new EnemyData());
        EnemyData enemyData = enemyDataMap.get(enemyName);
        enemyData.update(e);

        doAntiGravityMovement();
        aimAndFire(e);
    }

    public void doAntiGravityMovement() {
        double xForce = 0;
        double yForce = 0;

        // Repulsão das paredes
        xForce += GRAVITY_FORCE / Math.pow(getX() - WALL_MARGIN, 2);
        xForce -= GRAVITY_FORCE / Math.pow(getBattleFieldWidth() - getX() - WALL_MARGIN, 2);
        yForce += GRAVITY_FORCE / Math.pow(getY() - WALL_MARGIN, 2);
        yForce -= GRAVITY_FORCE / Math.pow(getBattleFieldHeight() - getY() - WALL_MARGIN, 2);

        // Repulsão de inimigos detectados
        if (lastScannedRobot != null) {
            double angleToEnemy = getHeadingRadians() + Math.toRadians(lastScannedRobot.getBearing());
            double enemyX = getX() + Math.sin(angleToEnemy) * lastScannedRobot.getDistance();
            double enemyY = getY() + Math.cos(angleToEnemy) * lastScannedRobot.getDistance();

            double force = GRAVITY_FORCE / Math.pow(lastScannedRobot.getDistance(), 2);
            xForce -= (enemyX - getX()) * force / lastScannedRobot.getDistance();
            yForce -= (enemyY - getY()) * force / lastScannedRobot.getDistance();
        }

        // Calcula a direção e distância final
        double moveAngle = Math.atan2(xForce, yForce);
        double moveDistance = Math.min(MAX_FORCE, Math.hypot(xForce, yForce));

        if (moveDistance < 50) {
            moveDistance = 50;
        }

        setTurnRightRadians(Utils.normalRelativeAngle(moveAngle - getHeadingRadians()));
        setAhead(moveDistance);
    }

    public void aimAndFire(ScannedRobotEvent e) {
        String enemyName = e.getName();
        EnemyData enemyData = enemyDataMap.get(enemyName);
        if (enemyData == null) return;

        double predictedX = enemyData.predictX(getX(), getY());
        double predictedY = enemyData.predictY(getX(), getY());

        double angleToTarget = Math.atan2(predictedX - getX(), predictedY - getY());
        double gunTurn = Utils.normalRelativeAngleDegrees(Math.toDegrees(angleToTarget) - getGunHeading());
        setTurnGunRight(gunTurn);

        double distance = e.getDistance();
        double firePower = calculateFirePower(distance);

        if (Math.abs(gunTurn) < 10) {
            fire(firePower);
        }

        // Realiza uma manobra evasiva após disparar
        setTurnRight(30 - Math.random() * 60); // Variação aleatória no movimento
        setAhead(50 + Math.random() * 100); // Distância aleatória
    }

    private double calculateFirePower(double distance) {
        if (distance < 150) {
            return MAX_BULLET_POWER; // Tiro forte
        } else if (distance < 500) {
            return (MAX_BULLET_POWER + MIN_BULLET_POWER) / 2; // Tiro médio
        } else {
            return MIN_BULLET_POWER; // Tiro fraco
        }
    }

    @Override
    public void onHitByBullet(HitByBulletEvent e) {
        setTurnRight(Utils.normalRelativeAngleDegrees(90 - e.getBearing()));
        setAhead(150);
    }

    @Override
    public void onHitWall(HitWallEvent e) {
        setTurnRight(180);
        setAhead(100);
    }

    // Classe para armazenar dados sobre os inimigos
    private static class EnemyData {
        private double lastHeading;
        private double lastVelocity;
        private long lastUpdateTime;

        public void update(ScannedRobotEvent e) {
            lastHeading = e.getHeadingRadians();
            lastVelocity = e.getVelocity();
            lastUpdateTime = e.getTime();
        }

        public double predictX(double currentX, double currentY) {
            double timeElapsed = getTimeElapsed();
            return currentX + Math.sin(lastHeading) * lastVelocity * timeElapsed;
        }

        public double predictY(double currentX, double currentY) {
            double timeElapsed = getTimeElapsed();
            return currentY + Math.cos(lastHeading) * lastVelocity * timeElapsed;
        }

        private double getTimeElapsed() {
            return 1; // Sempre estimar 1 turno de previsão
        }
    }
}
