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
		

	    
// RUDOLPH  -  RADAR //

	    
        // **Radar**: Travar no inimigo
        double radarTurn = robocode.util.Utils.normalRelativeAngleDegrees(absoluteBearing - getRadarHeading());
        setTurnRadarRight(radarTurn);
	    

	    
// THIAGO  -  TIRO //

      

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


	    
// LUCAS  -  MOVIMENTO //

	    
      
        // **Movimento**: Estratégia para desviar
        if (distance < 150) {
            setTurnRight(enemyBearing + 90); // Ficar a 90 graus do inimigo
            setAhead(100); // Avançar
        } else {
            setTurnRight(180);
			ahead(180);
			setTurnLeft(180);
			ahead(180);
        }
		
    }


	    
// GIULIANO  -  REAÇÃO A TIROS RECEBIDOS //

	    
      
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


	    
// INTEGRANTE X  -  MOVIMENTO EXTRA //

	    
      
    @Override
    public void onHitWall(HitWallEvent e) {
		// Atualização caso o robô bata na parede.
        if (e.getBearing() >= 0) 
		{
			turnLeft(e.getBearing() + 90);
			setTurnLeft(180);
			ahead(180);
			setTurnRight(180);
			ahead(180);
		}
		if (e.getBearing() <= 0)
		{
			turnRight(e.getBearing() + 90);
			setTurnRight(180);
			ahead(180);
			setTurnLeft(180);
			ahead(180);
		}
		ahead(50);
	}
}