package Garantido;

import robocode.*;
import robocode.util.Utils;
import java.awt.Color;
import java.util.HashMap;
import java.util.Map;

public class Caprichoso extends AdvancedRobot {

    private double previousEnergy = 100; // Energia anterior do inimigo
    private int moveDirection = 1; // Direção de movimento
    private Map<String, Double> enemyEnergyMap = new HashMap<>(); // Armazena a energia dos inimigos

    @Override
    public void run() {
        // Configurações iniciais
        setColors(Color.RED, Color.BLACK, Color.WHITE); // Corpo, arma, radar
        setAdjustGunForRobotTurn(true); // Desacopla a arma do movimento do robô
        setAdjustRadarForGunTurn(true); // Desacopla o radar da arma

        while (true) {
            setTurnRadarRight(Double.POSITIVE_INFINITY); // Mantém o radar girando
            execute();
        }
    }

    @Override
    public void onScannedRobot(ScannedRobotEvent e) {
        double distance = e.getDistance();
        double enemyBearing = e.getBearing();
        double absoluteBearing = getHeading() + enemyBearing;
        String enemyName = e.getName();

        // Atualiza a energia do inimigo (RUDOLPH)
        enemyEnergyMap.put(enemyName, e.getEnergy());

        // Focar no inimigo mais fragilizado (RUDOLPH)
        String weakestEnemy = getWeakestEnemy();
        if (weakestEnemy != null && weakestEnemy.equals(enemyName)) {
            // Mira no inimigo mais fragilizado (THIAGO)
            double gunTurn = Utils.normalRelativeAngleDegrees(absoluteBearing - getGunHeading());
            setTurnGunRight(gunTurn);

            if (Math.abs(gunTurn) < 5) {
                // Previsão de posição para disparo (THIAGO)
                fireAtPredictedPosition(e, distance);
            }
        }

        // Radar: Travar no inimigo (RUDOLPH)
        double radarTurn = Utils.normalRelativeAngleDegrees(absoluteBearing - getRadarHeading());
        setTurnRadarRight(radarTurn);

        // Movimentar-se em direção ao inimigo (LUCAS)
        moveTowardsEnemy(e);
    }

    private void fireAtPredictedPosition(ScannedRobotEvent e, double distance) {
        // Previsão de posição futura para tiro (THIAGO)
        double enemySpeed = e.getVelocity();
        double enemyHeading = e.getHeading();
        double absoluteEnemyBearing = getHeading() + e.getBearing();
        double enemyX = getX() + Math.sin(Math.toRadians(absoluteEnemyBearing)) * e.getDistance();
        double enemyY = getY() + Math.cos(Math.toRadians(absoluteEnemyBearing)) * e.getDistance();

        double timeToHit = distance / 14;
        double futureX = enemyX + Math.sin(Math.toRadians(enemyHeading)) * enemySpeed * timeToHit;
        double futureY = enemyY + Math.cos(Math.toRadians(enemyHeading)) * enemySpeed * timeToHit;

        double angleToFuturePosition = Math.atan2(futureX - getX(), futureY - getY());
        double gunTurnFuture = Utils.normalRelativeAngleDegrees(Math.toDegrees(angleToFuturePosition) - getGunHeading());
        setTurnGunRight(gunTurnFuture);

        // Ajusta a potência do tiro com base na distância (THIAGO)
        double firePower = calculateFirePower(distance);
        fire(firePower);
    }

    private double calculateFirePower(double distance) {
        // Calcula a potência ideal para o tiro (THIAGO)
        if (getEnergy() < 15) {
            return 1;
        } else if (distance < 200) {
            return 3;
        } else if (distance < 500) {
            return 2;
        } else {
            return 1;
        }
    }

    private void moveTowardsEnemy(ScannedRobotEvent e) {
        // Movimenta-se em direção ao inimigo para manter pressão (LUCAS)
        double distance = e.getDistance();
        double enemyBearing = e.getBearing();

        if (distance > 100) {
            setTurnRight(enemyBearing);
            setAhead(100);
        } else {
            setTurnRight(30 - Math.random() * 60);
            setAhead(100 * moveDirection);
        }
    }

    private String getWeakestEnemy() {
        // Seleciona o inimigo com menor energia (RUDOLPH)
        return enemyEnergyMap.entrySet().stream()
            .min(Map.Entry.comparingByValue())
            .map(Map.Entry::getKey)
            .orElse(null);
    }

    @Override
    public void onHitWall(HitWallEvent e) {
        // Ajusta a movimentação ao colidir com paredes (LUCAS)
        if (e.getBearing() >= 0) {
            turnLeft(e.getBearing() + 90);
            setTurnLeft(180);
            ahead(180);
            setTurnRight(180);
            ahead(180);
        } else {
            turnRight(e.getBearing() + 90);
            setTurnRight(180);
            ahead(180);
            setTurnLeft(180);
            ahead(180);
        }
        ahead(50);
    }

    @Override
    public void onHitByBullet(HitByBulletEvent e) {
        // Reage a tiros recebidos com movimentação evasiva (LUCAS)
        setTurnRight(90 - e.getBearing() + (Math.random() * 60 - 30));
        setAhead(150 * moveDirection);
        moveDirection *= -1;
    }
}