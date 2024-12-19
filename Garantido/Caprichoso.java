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
		
		private double lastEnergy = 100.0; // Armazena a energia do robô antes de ser atingido
	    private String lastAttacker = "";  // Armazena o nome do inimigo que atacou

        // Mantém o radar girando constantemente
        while (true) {
            setTurnRadarRight(Double.POSITIVE_INFINITY); // Radar nunca para
            if (getEnergy() < 30) {
                evasiveManeuver(); // Fazer manobra evasiva
            } else {
                // Se tiver energia suficiente, buscar atacar
                if (lastAttacker != "") {
                    attack(lastAttacker); // Priorizar atacar o inimigo que atacou
                } else {
                    moveRandomly(); // Movimento aleatório se não há inimigos específicos
                }
            }
            execute(); // Executa o movimento atual
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

    // Método que implementa a manobra evasiva
    private void evasiveManeuver() {
        setTurnRight(90); // Giro rápido para dificultar o alvo
        setAhead(100); // Avançar para frente
        setTurnLeft(90); // Outro giro rápido
    }

    private void moveRandomly() {
        // Variáveis de movimento aleatório
        double randomTurn = Math.random() * 180 - 90; // Gira aleatoriamente entre -90 e 90 graus
        double randomDistance = Math.random() * 200; // Distância aleatória de 0 a 200 pixels
        double randomSpeed = Math.random() * 2 + 1; // Velocidade aleatória de 1 a 3 (tamanho do passo)

        // Alterna o movimento entre frente e atrás
        if (Math.random() > 0.5) {
            setAhead(randomDistance); // Avança uma distância aleatória
        } else {
            setBack(randomDistance); // Vai para trás
        }

        // Gira aleatoriamente à esquerda ou à direita
        setTurnRight(randomTurn); // Gira aleatoriamente entre -90 e 90 graus

        // Ajusta a velocidade de movimento
        setMaxVelocity(randomSpeed); // Aumenta ou diminui a velocidade

        // Se o robô estiver próximo das bordas do campo, evita sair do campo
        if (getX() < 50 || getX() > getBattleFieldWidth() - 50 || getY() < 50 || getY() > getBattleFieldHeight() - 50) {
            // Reverte para o centro do campo ou muda de direção para evitar a borda
            setTurnRight(180); // Gira 180º para mudar de direção
            setAhead(150); // Avança para fora da borda
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


	    
// INTEGRANTE X  -  MOVIMENTO EXTRA //

	    
      
    @Override
    public void onHitWall(HitWallEvent e) {
        // Reação ao bater na parede: recua e vira
        back(50); // Dá ré
        setTurnRight(90); // Gira pra se afastar
		}
	
}