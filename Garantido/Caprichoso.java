package Garantido;

import robocode.*;
import java.awt.Color;

public class Caprichoso extends AdvancedRobot {

    @Override
    public void run() {
        // Configurações iniciais
        setColors(Color.RED, Color.BLACK, Color.WHITE); // Corpo, arma, radar
        setAdjustGunForRobotTurn(true); // Desacopla a arma do movimento do robô
        setAdjustRadarForGunTurn(true); // Desacopla o radar da arma

        // Manobra de radar e movimento agressivo
        while (true) {
            setTurnRadarRight(Double.POSITIVE_INFINITY); // Radar gira indefinidamente
            execute(); // Executa o movimento atual
        }
    }

    @Override
    public void onScannedRobot(ScannedRobotEvent e) {
        double distance = e.getDistance(); // Distância do inimigo
        double enemyBearing = e.getBearing(); // Direção do inimigo em relação ao robô
        double absoluteBearing = getHeading() + enemyBearing; // Direção absoluta

        // Radar: Travar no inimigo
        double radarTurn = robocode.util.Utils.normalRelativeAngleDegrees(absoluteBearing - getRadarHeading());
        setTurnRadarRight(radarTurn);

        // Canhão: Mira no inimigo
        double gunTurn = robocode.util.Utils.normalRelativeAngleDegrees(absoluteBearing - getGunHeading());
        setTurnGunRight(gunTurn);

        // Tiro com base na posição futura do inimigo
        if (Math.abs(gunTurn) < 5) {
            fireAtPredictedPosition(e, distance);
        }

        // Movimentação agressiva
        moveTowardsEnemy(e);
    }

    private void fireAtPredictedPosition(ScannedRobotEvent e, double distance) {
        // Se a distância for pequena, aumenta a potência do tiro
        double firePower = (distance < 150) ? 3 : (distance < 300) ? 2 : 1;
        fire(firePower);
    }

    private void moveTowardsEnemy(ScannedRobotEvent e) {
        double distance = e.getDistance();
        double enemyBearing = e.getBearing();
        
        // Movimentação agressiva: mova-se em direção ao inimigo
        if (distance > 100) {
            setTurnRight(enemyBearing); // Ajusta a direção para o inimigo
            setAhead(100); // Avança em direção ao inimigo
        } else {
            setTurnRight(enemyBearing); // Ajusta a direção para o inimigo
            setAhead(50); // Avança mais devagar quando muito perto
        }
    }

    @Override
    public void onHitByBullet(HitByBulletEvent e) {
        // Em caso de ser atingido, mantém a agressividade, mas muda a estratégia
        double bearing = e.getBearing(); // De onde veio o tiro
        setTurnRight(-bearing); // Gira na direção oposta
        setAhead(100); // Avança rapidamente
        turnGunRight(robocode.util.Utils.normalRelativeAngleDegrees(getHeading() - getGunHeading()));
        fire(2); // Tiro mais forte de volta
    }

    @Override
    public void onHitWall(HitWallEvent e) {
        // Estratégia para se desviar de paredes
        setTurnRight(180); // Giro de 180º para mudar de direção
        setAhead(100); // Avança para se afastar da parede
    }
}
