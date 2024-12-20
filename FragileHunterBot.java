import robocode.*;
import robocode.util.Utils;
import java.util.ArrayList;
import java.util.List;

public class FragileHunterBot extends AdvancedRobot {
    
    private List<EnemyInfo> enemies = new ArrayList<>();
    
    public void run() {
        // Definir as cores do robô
        setColors(java.awt.Color.RED, java.awt.Color.BLACK, java.awt.Color.BLUE);
        
        // Loop principal para movimentação
        while (true) {
            setTurnRadarRight(360); // Manter o radar girando continuamente
            execute();
        }
    }
    
    public void onScannedRobot(ScannedRobotEvent e) {
        updateEnemy(e); // Atualiza a lista de inimigos

        // Encontrar o inimigo mais fragilizado
        EnemyInfo weakestEnemy = getWeakestEnemy();

        if (weakestEnemy != null) {
            // Calcular ângulo para o inimigo mais fragilizado
            double angleToEnemy = getHeading() + e.getBearing() - getGunHeading();
            setTurnGunRight(Utils.normalRelativeAngleDegrees(angleToEnemy));

            // Ajustar potência do disparo baseado na distância
            double firePower = Math.min(3.0, Math.max(0.1, 500 / e.getDistance()));

            // Disparar se o canhão estiver alinhado
            if (getGunHeat() == 0 && Math.abs(getGunTurnRemaining()) < 5) {
                setFire(firePower);
            }

            // Movimentação para evitar previsibilidade
            if (e.getDistance() > 150) {
                setAhead(100);
            } else {
                setTurnRight(90);
                setBack(50);
            }
        }
    }
    
    public void onRobotDeath(RobotDeathEvent e) {
        // Remover inimigos eliminados da lista
        enemies.removeIf(enemy -> enemy.name.equals(e.getName()));
    }

    private void updateEnemy(ScannedRobotEvent e) {
        for (EnemyInfo enemy : enemies) {
            if (enemy.name.equals(e.getName())) {
                enemy.energy = e.getEnergy();
                return;
            }
        }
        enemies.add(new EnemyInfo(e.getName(), e.getEnergy()));
    }
    
    private EnemyInfo getWeakestEnemy() {
        EnemyInfo weakest = null;
        for (EnemyInfo enemy : enemies) {
            if (weakest == null || enemy.energy < weakest.energy) {
                weakest = enemy;
            }
        }
        return weakest;
    }

    // Classe auxiliar para armazenar informações sobre os inimigos
    private class EnemyInfo {
        String name;
        double energy;

        EnemyInfo(String name, double energy) {
            this.name = name;
            this.energy = energy;
        }
    }
}
