
var conn = { };
var board;
var ctx;

var orientation = 0;
var tx = 0, ty = 0;
var ax = 0, ay = 0;
var bx = 0, by = 0;
var rx = 0, ry = 0;
var energy = MAX_ENERGY;
var health = MAX_HEALTH;
var shooting = false;
var bullets = new Array();
var bulletsFired = 0;
//var exploded = false;
var stones = null;
var dirt = null;

var eorientation = -1;
var etx = 0, ety = 0;
var ebx = 0, eby = 0;
var enemyBullets = new Array();

var deaths = 0, enemyDeaths = 0;


var pressed = 0;
var timer = null;
var shootCtr = 0;

var tankImg, enemyTankImg;
//tankImg.src= 'file:///home/marek/work/webtunneler/tank.png';
var explAudio;


function init() {
  board =  document.getElementById('board');
  ctx = board.getContext('2d');
  tankImg = new Image();
  enemyTankImg = new Image();
  tankImg.src= 'http://localhost/wt/img/tank.png';
  enemyTankImg.src= 'http://localhost/wt/img/enemytank.png';
  explAudio = document.getElementById('expl');

  if (window.WebSocket === undefined) {
    alert('Sockets not supported');
  } else {
    openConnection();
  }
}

function openConnection() {
  if (conn.readyState === undefined || conn.readyState > 1) {
    //conn = new WebSocket('ws://localhost:8787', 'custom');
    //conn = new WebSocket('ws://localhost:8787', 'json');
    //conn = new WebSocket('ws://localhost:8787', 'text');
    //conn = new WebSocket('ws://localhost:8787', 'custom/text');
    //conn = new WebSocket('ws://localhost:8787/?;prot=custom');
    //conn = new WebSocket('ws://localhost:8787/?;prot=text');
    //conn = new WebSocket('ws://localhost:8787/?;subprot=custom/text');
    //conn = new WebSocket('ws://localhost:8787/');//, 'custom/text');
    conn = new WebSocket('ws://localhost:8080/websockets/ws');
    conn.onopen = function () {
      alert('Socket open');
      var prefix = (window.location.href.indexOf('new') >= 0) ? 'NEW' : 'JOIN';
      sendInit(prefix);
      doTimer();
    };

    conn.onmessage = processPacket;
  
    conn.onclose = function (event) {
      alert('Socket closed');
    };
  }
}

function processPacket(event) {
  //alert(event.data);
  //ss = event.data.split(':');
  //var data = eval('(' + event.data + ')');
  var data = JSON.parse(event.data);
  //alert(event.data);
  //if (ss[0] == 'INIT') {  
  if (data.cmd == 'SCENE') {
    initBoard(data);
    return;
  }
  //if (ss[0] == 'EXPL') {
  //if (data.cmd == 'EXPL') {
  if (data.eor == 99) {
    enemyDeaths++;
    explode();
    return;
  }
  rx = data.rx;
  ry = data.ry;

  eorientation = data.eor;
  etx = data.ex;
  ety = data.ey;
  var enemyBulletsFired = data.eb;
  for (var i = 0; i < enemyBulletsFired; i++) {
    enemyBullets.push(newBullet(etx, ety, eorientation));
  }
  
  for (var di in data.dirtRemoved) {
    var d = data.dirtRemoved[di];
    dirt[d.x][d.y] = false;
  }
}

function doTimer() {
  timer = setTimeout("doTimer()", TIMER_INT);
  updatePos();
  recharge();
  
  checkBullets();
  if (shooting) {
    if (shootCtr == 0) {
      shootBullets();
      shootCtr = SHOOT_DIV;
    } else {
      shootCtr--;
    }
  }
  
  sendPos();
  draw();
}

function recharge() {
  if ((rx >= bx) && (rx < bx+BASE_WIDTH) && (ry >= by) && (ry < by+BASE_HEIGHT)) {
    if (health < MAX_HEALTH) {health += HEALTH_INC; if (health > MAX_HEALTH) {health = MAX_HEALTH;}}
    if (energy < MAX_ENERGY) {energy += ENERGY_INC; if (energy > MAX_ENERGY) {energy = MAX_ENERGY;}}
  }
}


function initBoard(data) {
  bx = data.bx;
  by = data.by;
  tx = rx = bx + BASE_WIDTH/2;
  ty = ry = by + BASE_HEIGHT/2;

  ebx = data.ebx;
  eby = data.eby;
  
  health = MAX_HEALTH;
  energy = MAX_ENERGY;

  stones = data.stones;

  dirt = new Array();
  for (var x = 0; x < DIRT_X_CNT; x++) {
    dirt[x] = new Array();
    for (var y = 0; y < DIRT_Y_CNT; y++) {
      dirt[x][y] = true;
    }
  }
}

function updatePos() {
  switch (pressed) {
    case 0:
      break;
    case 1:
      orientation = 6;
      tx -= INCR_RECT;
      break;
    case 2:
      orientation = 0;
      ty -= INCR_RECT;
      break;
    case 3:
      orientation = 7;
      tx -= INCR_DIAG; ty -= INCR_DIAG;
      break;
    case 4:
      orientation = 2;
      tx += INCR_RECT;
      break;
    case 6:
      orientation = 1;
      tx += INCR_DIAG; ty -= INCR_DIAG;
      break;
    case 8:
      orientation = 4;
      ty += INCR_RECT;
      break;
    case 9:
      orientation = 5;
      tx -= INCR_DIAG; ty += INCR_DIAG;
      break;
    case 12:
      orientation = 3;
      tx += INCR_DIAG; ty += INCR_DIAG;
      break;
  }
  if (tx < TANK_R) {tx = TANK_R;} else if (tx > ARENA_WIDTH-TANK_R) {tx = ARENA_WIDTH-TANK_R;}
  if (ty < TANK_R) {ty = TANK_R;} else if (ty > ARENA_HEIGHT-TANK_R) {ty = ARENA_HEIGHT-TANK_R;}
}


function sendInit(cmd) {
  conn.send("{id: 1; cmd: "+cmd+"}");
}

function sendPos() {
  if (conn.readyState !== 1) {return;}
  conn.send("{or:"+orientation+";x:"+tx+";y:"+ty+";b:"+bulletsFired+"}");
  bulletsFired = 0;
}

function sendExpl() {
  if (conn.readyState !== 1) {return;}
  conn.send("{or:99;x:"+tx+";y:"+ty+";b:"+bulletsFired+"}");
  bulletsFired = 0;
  //conn.send("EXPL");
}

function keyDown(e) {
  var c = e.keyCode;
  if ((c >= 37) && (c <= 40)) {
    pressed |= 1 << (c-37);
  } else if (c == 17) {
    if (!shooting) {shootCtr = 0;}
    shooting = true;
  }
}

function keyUp(e) {
  var c = e.keyCode;
  if ((c >= 37) && (c <= 40)) {
    pressed &= ~(1 << (c-37));
  } else if (c == 17) {
    shooting = false;
  }
}

function explode() {
  explAudio.play();
}
