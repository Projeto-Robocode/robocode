package Garantido;

import robocode.*;
import java.awt.Color;

public class Caprichado extends AdvancedRobot {
    
    @Override
    public void run() {
        // Configurações iniciais
        setColors(Color.RED, Color.BLACK, Color.WHITE); // Corpo, arma, radar
        setAdjustGunForRobotTurn(true); // Desacopla a arma do movimento do robô
        setAdjustRadarForGunTurn(true); // Desacopla o radar da arma

        // Mantém o radar girando constantemente
        while (true) {
            setTurnRadarRight(Double.POSITIVE_INFINITY); // Radar nunca para
            execute();
        }
    }

    @Override
    public void onScannedRobot(ScannedRobotEvent e) {
        double distance = e.getDistance(); // Distância do inimigo
        double enemyBearing = e.getBearing(); // Direção do inimigo em relação ao robô
        double absoluteBearing = getHeading() + enemyBearing; // Direção absoluta

        // **Radar**: Travar no inimigo
        double radarTurn = robocode.util.Utils.normalRelativeAngleDegrees(absoluteBearing - getRadarHeading());
        setTurnRadarRight(radarTurn);

        // **Arma**: Mira no inimigo
        double gunTurn = robocode.util.Utils.normalRelativeAngleDegrees(absoluteBearing - getGunHeading());
        setTurnGunRight(gunTurn);

        // **Tiro**: Ajustar potência com base na distância
        if (distance < 200) {
            fire(3); // Potência máxima para alvos próximos
        } else if (distance < 500) {
            fire(2); // Potência média
        } else {
            fire(1); // Potência mínima para alvos distantes
        }

        // **Movimento**: Estratégia para desviar
        if (distance < 150) {
            setTurnRight(enemyBearing + 90); // Ficar a 90 graus do inimigo
            setAhead(100); // Avançar
        } else {
            setTurnRight(30); // Movimentos mais aleatórios
            setAhead(200);
        }
    }

    @Override
    public void onHitByBullet(HitByBulletEvent e) {
        // Reação ao ser atingido: movimento evasivo
        double bearing = e.getBearing(); // De onde veio o tiro
        setTurnRight(-bearing); // Gira na direção oposta
        setAhead(150); // Avança rapidamente
        // Atira de volta se possível
        turnGunRight(robocode.util.Utils.normalRelativeAngleDegrees(getHeading() - getGunHeading()));
        fire(1.5); // Tiro médio como reação
    }

    @Override
    public void onHitWall(HitWallEvent e) {
        // Reação ao bater na parede: recua e vira
        back(50); // Dá ré
        setTurnRight(90); // Gira pra se afastar
		}
	
}
