
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
var newBullets = new Array();
//var bulletsFired = 0;
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
var explAudio;

//var SERVER = 'malina.felk.cvut.cz:8080';
var SERVER = 'localhost:8080';


function init() {
  board =  document.getElementById('board');
  ctx = board.getContext('2d');
  tankImg = new Image();
  enemyTankImg = new Image();
  tankImg.src= 'http://'+SERVER+'/wt/img/tank.png';
  enemyTankImg.src= 'http://'+SERVER+'/wt/img/enemytank.png';
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
    //conn = new WebSocket('ws://'+SERVER+'/websockets/wts');
    conn = new WebSocket('ws://'+SERVER+'/wt/wts');
    conn.onopen = function () {
      console.log('Socket open');
      //var cmd = (window.location.href.indexOf('new') >= 0) ? 'NEW' : 'JOIN';
      var cmd = newGame ? 'NEW' : 'JOIN';
      sendInit(gameId, cmd);
      doTimer();
    };

    conn.onmessage = processPacket;
  
    conn.onclose = function (event) {
      console.log('Socket closed');
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

  eorientation = data.eor;
  etx = data.ex;
  ety = data.ey;
  //var enemyBulletsFired = data.eb;
  var enemyBullets = enemyBullets.concat(data.eb);
  /*for (var i = 0; i < enemyBulletsFired; i++) {
    enemyBullets.push(newBullet(etx, ety, eorientation));
  }*/
  
  for (var di in data.drem) {
    var d = data.drem[di];
    dirt[d.x][d.y] = false;
  }
}

function doTimer() {
  //alert('doTimer')
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


function sendInit(id, cmd) {
    var s = "{id: "+id+"; cmd: '"+cmd+"'}";
    console.log('sending init: '+s);
    var res = conn.send(s);
    console.log('sent init: '+res);
}

function sendPos() {
  if (conn.readyState !== 1) {return;}
  conn.send("{or:"+orientation+";x:"+tx+";y:"+ty+";b:"+JSON.stringify(newBullets)+"}");
  bulletsFired = 0;
}

/*function sendExpl() {
  if (conn.readyState !== 1) {return;}
  conn.send("{or:"+ORIENTATION_EXPLODED+";x:"+tx+";y:"+ty+";b:"+bulletsFired+"}");
  bulletsFired = 0;
  //conn.send("EXPL");
}*/

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
