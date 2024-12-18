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

	    
// RUDOLPH  -  RADAR //

	    
        // **Radar**: Travar no inimigo
        double radarTurn = robocode.util.Utils.normalRelativeAngleDegrees(absoluteBearing - getRadarHeading());
        setTurnRadarRight(radarTurn);
	    

	    
// THIAGO  -  TIRO //

      

   // canhão mira no inimigo
double gunTurn = robocode.util.Utils.normalRelativeAngleDegrees(absoluteBearing - getGunHeading());
setTurnGunRight(gunTurn);

// verifica o ângulo entre canhão e inimigo
if (Math.abs(gunTurn) < 5) {
    // calcula posição futura com base na velocidade e direção
    double enemySpeed = e.getVelocity(); 
    double enemyHeading = e.getHeading(); 

    // calcula a posição atual 
    double absoluteEnemyBearing = getHeading() + e.getBearing(); 
    double enemyX = getX() + Math.sin(Math.toRadians(absoluteEnemyBearing)) * e.getDistance();
    double enemyY = getY() + Math.cos(Math.toRadians(absoluteEnemyBearing)) * e.getDistance();

    // calcula tempo até que o disparo atinja o inimigo
    double timeToHit = distance / 14;

    // calcula a posição futura do inimigo
    double futureX = enemyX + Math.sin(Math.toRadians(enemyHeading)) * enemySpeed * timeToHit;
    double futureY = enemyY + Math.cos(Math.toRadians(enemyHeading)) * enemySpeed * timeToHit;

    // calcula a direção para a posição futura do inimigo
    double angleToFuturePosition = Math.atan2(futureX - getX(), futureY - getY());
    double gunTurnFuture = robocode.util.Utils.normalRelativeAngleDegrees(Math.toDegrees(angleToFuturePosition) - getGunHeading());

    // Ajuste a arma para apontar para a posição futura do inimigo
    setTurnGunRight(gunTurnFuture);

    // Ajustar potência com base na distância
    if (distance < 200) {
        fire(3);
    } else if (distance < 500) {
        fire(2);
    } else {
        fire(1);
    }
}


	    
// LUCAS  -  MOVIMENTO //

	    
      
        // **Movimento**: Estratégia para desviar
        if (distance < 150) {
            setTurnRight(enemyBearing + 90); // Ficar a 90 graus do inimigo
            setAhead(100); // Avançar
        } else {
            setTurnRight(30); // Movimentos mais aleatórios
            setAhead(200);
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
        // Reação ao bater na parede: recua e vira
        back(50); // Dá ré
        setTurnRight(90); // Gira pra se afastar
		}
	
}
