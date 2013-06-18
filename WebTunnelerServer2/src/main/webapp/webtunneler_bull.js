
var bulletId = 0;

function newBullet(cx, cy, orientation) {
    var posX = cx, posY = cy;
    switch (orientation) {
	case 0:
	    posY -= TANK_H2;
	    break;
	case 1:
	    posX += TANK_DIAG;
	    posY -= TANK_DIAG;
	    break;
	case 2:
	    posX += TANK_H2;
	    break;
	case 3:
	    posX += TANK_DIAG;
	    posY += TANK_DIAG;
	    break;
	case 4:
	    posY += TANK_H2;
	    break;
	case 5:
	    posX -= TANK_DIAG;
	    posY += TANK_DIAG;
	    break;
	case 6:
	    posX -= TANK_H2;
	    break;
	case 7:
	    posX -= TANK_DIAG;
	    posY -= TANK_DIAG;
	    break;
    }
    return {id: (bulletId++), or: orientation, x: posX, y: posY};
}

function moveBullet(b) {
  switch (b.or) {
    case 0:
	b.y -= SHOOT_INCR_RECT;
	break;
    case 1:
	b.x += SHOOT_INCR_DIAG;
	b.y -= SHOOT_INCR_DIAG;
	break;
    case 2:
	b.x += SHOOT_INCR_RECT;
	break;
    case 3:
	b.x += SHOOT_INCR_DIAG;
	b.y += SHOOT_INCR_DIAG;
	break;
    case 4:
	b.y += SHOOT_INCR_RECT;
	break;
    case 5:
	b.x -= SHOOT_INCR_DIAG;
	b.y += SHOOT_INCR_DIAG;
	break;
    case 6:
	b.x -= SHOOT_INCR_RECT;
	break;
    case 7:
	b.x -= SHOOT_INCR_DIAG;
	b.y -= SHOOT_INCR_DIAG;
	break;
  }
  return (b.x >= 0) && (b.y >= 0) && (b.x < ARENA_WIDTH) && (b.y < ARENA_HEIGHT);
}

function shootBullets() {
  if (energy < BULLET_ENERGY) {return;}
  var b = newBullet(rx, ry, orientation);
  //bullets.push(b);
  newBullets.push(b);
  bullets[b.id] = b;
}

function checkBullets() {
  for (var i in bullets) {
    var b = bullets[i];
    if (!moveBullet(b)) {delete bullets[i];}
  }
  for (var i in enemyBullets) {
    var b = enemyBullets[i];
    if (!moveBullet(b)) {delete enemyBullets[i];}
  }
}
