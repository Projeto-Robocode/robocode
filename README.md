# robocode

## Anti-Gravidade (Gravitacional):
Usar padrões baseados em probabilidades: Robôs campeões tentam prever a posição do inimigo e ajustar suas estratégias de tiro com base na probabilidade de acerto. Isso envolve métodos mais complexos, como modelos probabilísticos de trajetória, em vez de depender apenas de cálculos de ângulo e velocidade.

"Vou aplicar o conceito de anti-gravidade de maneira simples no seu código. A ideia é que o robô se mova automaticamente para longe de inimigos e das paredes, como se fossem "fontes de repulsão". O movimento será básico, sem cálculos complexos."

-----------------------------------------------------

// Variáveis globais (se necessário)
double battlefieldWidth = getBattleFieldWidth();
double battlefieldHeight = getBattleFieldHeight();

@Override
public void run() {
    // Loop principal
    while (true) {
        antiGravityMove();
        execute(); // Executa os comandos de movimento
    }
}

// Função de anti-gravidade
public void antiGravityMove() {
    double xForce = 0;
    double yForce = 0;

    // Repulsão das paredes
    xForce += 1000 / Math.pow(getX(), 2); // Força da parede esquerda
    xForce -= 1000 / Math.pow(battlefieldWidth - getX(), 2); // Força da parede direita
    yForce += 1000 / Math.pow(getY(), 2); // Força da parede inferior
    yForce -= 1000 / Math.pow(battlefieldHeight - getY(), 2); // Força da parede superior

    // Repulsão do inimigo (usar último scan do radar)
    ScannedRobotEvent e = getLastScannedRobotEvent();
    if (e != null) {
        double angleToEnemy = Math.toRadians(getHeading() + e.getBearing());
        double enemyX = getX() + Math.sin(angleToEnemy) * e.getDistance();
        double enemyY = getY() + Math.cos(angleToEnemy) * e.getDistance();

        // Adiciona força de repulsão do inimigo
        double distance = e.getDistance();
        xForce -= 5000 / Math.pow(enemyX - getX(), 2);
        yForce -= 5000 / Math.pow(enemyY - getY(), 2);
    }

    // Calcula a direção para mover
    double angle = Math.atan2(yForce, xForce);
    double moveX = Math.cos(angle) * 100; // Multiplicador para ajustar a velocidade
    double moveY = Math.sin(angle) * 100;

    // Move o robô
    setTurnRightRadians(angle - Math.toRadians(getHeading())); // Gira para o ângulo de movimento
    setAhead(Math.sqrt(moveX * moveX + moveY * moveY)); // Move para frente
}

// Método para capturar o último evento de escaneamento (necessário)
private ScannedRobotEvent lastScannedEvent;

@Override
public void onScannedRobot(ScannedRobotEvent e) {
    lastScannedEvent = e;
}

public ScannedRobotEvent getLastScannedRobotEvent() {
    return lastScannedEvent;
}
-----------------------------------------------------
