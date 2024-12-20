package Garantido;

import robocode.*;
import robocode.util.Utils;
import java.awt.Color;

public class Garantido extends AdvancedRobot {

    private static final double GRAVITY_FORCE = 1000; // Força de repulsão
    private static final double WALL_MARGIN = 50; // Margem de distância da parede
    private static final double MAX_FORCE = 4000; // Limita a força total para não sobrecarregar

    private ScannedRobotEvent lastScannedRobot;

    @Override
    public void run() {
        // Configurações iniciais
        setColors(Color.BLUE, Color.WHITE, Color.BLACK); // Corpo, arma, radar
        setAdjustGunForRobotTurn(true); // Desacopla a arma do movimento do robô
        setAdjustRadarForGunTurn(true); // Desacopla o radar da arma

        while (true) {
            doAntiGravityMovement();
            execute(); // Processa as ações acumuladas
        }
    }

    @Override
    public void onScannedRobot(ScannedRobotEvent e) {
        lastScannedRobot = e; // Armazena o evento mais recente
        doAntiGravityMovement();
    }

    public void doAntiGravityMovement() {
        double xForce = 0;
        double yForce = 0;

        // 1. Repulsão das paredes
        xForce += GRAVITY_FORCE / Math.pow(getX() - WALL_MARGIN, 2);
        xForce -= GRAVITY_FORCE / Math.pow(getBattleFieldWidth() - getX() - WALL_MARGIN, 2);
        yForce += GRAVITY_FORCE / Math.pow(getY() - WALL_MARGIN, 2);
        yForce -= GRAVITY_FORCE / Math.pow(getBattleFieldHeight() - getY() - WALL_MARGIN, 2);

        // 2. Repulsão de inimigos detectados
        if (lastScannedRobot != null) {
            double angleToEnemy = getHeadingRadians() + Math.toRadians(lastScannedRobot.getBearing());
            double enemyX = getX() + Math.sin(angleToEnemy) * lastScannedRobot.getDistance();
            double enemyY = getY() + Math.cos(angleToEnemy) * lastScannedRobot.getDistance();

            double force = GRAVITY_FORCE / Math.pow(lastScannedRobot.getDistance(), 2); // Repulsão proporcional à distância
            xForce -= (enemyX - getX()) * force / lastScannedRobot.getDistance();
            yForce -= (enemyY - getY()) * force / lastScannedRobot.getDistance();
        }

        // 3. Calcula a direção final
        double moveAngle = Math.atan2(xForce, yForce);
        double moveDistance = Math.min(MAX_FORCE, Math.hypot(xForce, yForce));

        // 4. Movimenta o robô
        setTurnRightRadians(Utils.normalRelativeAngle(moveAngle - getHeadingRadians()));
        setAhead(moveDistance);
    }

    @Override
    public void onHitByBullet(HitByBulletEvent e) {
        // Reage ao ser atingido movendo-se rapidamente para longe
        setTurnRight(Utils.normalRelativeAngleDegrees(90 - e.getBearing()));
        setAhead(150);
    }

    @Override
    public void onHitWall(HitWallEvent e) {
        // Gira e se afasta da parede
        setTurnRight(180);
        setAhead(100);
    }
}
