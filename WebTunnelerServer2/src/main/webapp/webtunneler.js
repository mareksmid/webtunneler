
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
var bullets = {};
var newBullets = new Array();
var stones = null;
var dirt = null;

var eorientation = -1;
var etx = 0, ety = 0;
var ebx = 0, eby = 0;
var enemyBullets = {};

var deaths = 0, enemyDeaths = 0;


var pressed = 0;
var timer = null;
var shootCtr = 0;

var tankImg, enemyTankImg;
var explAudio;


function init() {
  board =  document.getElementById('board');
  ctx = board.getContext('2d');
  tankImg = new Image();
  enemyTankImg = new Image();
  tankImg.src= '/wt/img/tank.png';
  enemyTankImg.src= '/wt/img/enemytank.png';
  explAudio = document.getElementById('expl');

  if (window.WebSocket === undefined) {
    alert('Sockets not supported');
  } else {
    openConnection();
  }
}

function openConnection() {
  if (conn.readyState === undefined || conn.readyState > 1) {
    conn = new WebSocket('ws://'+SERVER+'/wt/wts');
    conn.onopen = function () {
      console.log('Socket open');
      var cmd = newGame ? 'NEW' : 'JOIN';
      sendInit(gameId, cmd);
      //doTimer();
      timer = setInterval(doTimer, TIMER_INT);
    };

    conn.onmessage = processPacket;
  
    conn.onclose = function (event) {
      console.log('Socket closed');
    };
  }
}

function processPacket(event) {
  var data = JSON.parse(event.data);
  if (data.cmd == 'SCENE') {
    initBoard(data);
    return;
  }
  if (data.cmd == 'EEXPL') {
    enemyDeaths++;
    explode();
    return;
  }
  if (data.cmd == 'EXPL') {
    deaths++;
    explode();
    return;
  }
  tx = rx = data.x;
  ty = ry = data.y;
  energy = data.e;
  health = data.h;

  eorientation = data.eor;
  etx = data.ex;
  ety = data.ey;
  //enemyBullets = enemyBullets.concat(data.eb);
  for (var i in data.eb) {
      var b = data.eb[i];
      enemyBullets[b.id] = b;
  }
  
  for (var di in data.drem) {
    var d = data.drem[di];
    dirt[d.x][d.y] = false;
  }
  
  for (var bi in data.brem) {
      delete bullets[data.brem[bi]];
  }
  for (var bi in data.ebrem) {
      delete enemyBullets[data.ebrem[bi]];
  }
}

function doTimer() {
  updatePos();
  
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

function initBoard(data) {
  bx = data.bx;
  by = data.by;
  tx = rx = data.tx;
  ty = ry = data.ty;
  etx = data.etx;
  ety = data.ety;

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


function sendInit(id, cmd) {
    var s = "{id: "+id+"; cmd: '"+cmd+"'}";
    console.log('sending init: '+s);
    var res = conn.send(s);
    console.log('sent init: '+res);
}

function sendPos() {
  if (conn.readyState !== 1) {return;}
  conn.send("{or:"+orientation+";x:"+tx+";y:"+ty+";b:"+JSON.stringify(newBullets)+"}");
  newBullets = new Array();
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
