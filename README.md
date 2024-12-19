# Robô Caprichoso




## 1. Radar (Rudolph)

Responsável por localizar e rastrear os inimigos com eficiência.

**Objetivo:** Garantir que o radar fique sempre travado no inimigo, sem perder o alvo.

_Código principal:_

```
// Radar: Travar no inimigo
double radarTurn = robocode.util.Utils.normalRelativeAngleDegrees(absoluteBearing - getRadarHeading());
setTurnRadarRight(radarTurn);
```

_Possíveis melhorias:_

* Fazer o radar buscar novos inimigos automaticamente quando um inimigo sai do alcance.

* Adicionar priorização de inimigos com menor energia ou mais próximos.

* Implementar lógica pra o radar girar continuamente até achar um inimigo.




## 2. Movimento (Lucas)

Define como o robô se movimenta para atacar, desviar de tiros e evitar ficar previsível.

**Objetivo:** Tornar o robô difícil de acertar e posicioná-lo bem durante as batalhas.

_Código principal:_

```
if (distance < 150) {
    setTurnRight(enemyBearing + 90); // Ficar a 90 graus do inimigo
    setAhead(100); // Avançar
} else {
    setTurnRight(30); // Movimentos mais aleatórios
    setAhead(200);
}
```

_Possíveis melhorias:_

* Tornar os movimentos mais imprevisíveis com números aleatórios ou curvas.

* Criar estratégias específicas pra fugir quando a energia estiver baixa.

* Implementar "movimento oscilante" pra desviar de tiros previsíveis.




## 3. Tiro (Thiago)

Controla como e quando o robô atira, ajustando mira e potência.

**Objetivo:** Acertar o inimigo com precisão, economizando energia em tiros inúteis.

_Código principal:_

```
// Arma: Mira no inimigo
double gunTurn = robocode.util.Utils.normalRelativeAngleDegrees(absoluteBearing - getGunHeading());
setTurnGunRight(gunTurn);

// Tiro: Ajustar potência com base na distância
if (distance < 200) {
    fire(3); // Potência máxima para alvos próximos
} else if (distance < 500) {
    fire(2); // Potência média
} else {
    fire(1); // Potência mínima para alvos distantes
}
```

_Possíveis melhorias:_

* Prever o movimento do inimigo e atirar onde ele estará, não onde ele está agora.

* Ajustar a potência do tiro com base na energia restante do próprio robô.

* Criar estratégias de tiro mais agressivas contra inimigos fracos.


#### Melhoria 1:
Adicionado método para só atirar quando o ângulo entre a arma e o inimigo for menor de 5 graus.
```
if (Math.abs(gunTurn) < 5) {...}
```



## 4. Reação a tiros recebidos (Giuliano)

Define como o robô reage quando é atingido ou detecta perigo.

**Objetivo:** Minimizar o dano recebido e contra-atacar de forma eficiente.

_Código principal:_

```
// Reação ao ser atingido: movimento evasivo
double bearing = e.getBearing(); // De onde veio o tiro
setTurnRight(-bearing); // Gira na direção oposta
setAhead(150); // Avança rapidamente
// Atira de volta se possível
turnGunRight(robocode.util.Utils.normalRelativeAngleDegrees(getHeading() - getGunHeading()));
fire(1.5); // Tiro médio como reação
```

_Possíveis melhorias:_

* Implementar estratégias mais complexas de fuga, como giros e mudanças rápidas de direção.

* Decidir se deve atacar ou fugir dependendo da energia restante.

* Identificar o inimigo que atirou e priorizá-lo para ataques futuros.


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
