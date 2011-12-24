
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
    return [orientation, posX, posY];
}


function collides(b, x, y) {
  var x1 = x, x2 = x;
  var y1 = y, y2 = y;

  switch (orientation) {
      case 0:
      case 4:
	  x1 -= TANK_W2;
	  x2 += TANK_W2;
	  y1 -= TANK_H2;
	  y2 += TANK_H2;
	  break;
      case 1:
      case 5:
	  x1 -= TANK_DIAG;
	  x2 += TANK_DIAG;
	  y1 -= TANK_DIAG;
	  y2 += TANK_DIAG;
	  break;
      case 2:
      case 6:
	  x1 -= TANK_H2;
	  x2 += TANK_H2;
	  y1 -= TANK_W2;
	  y2 += TANK_W2;
	  break;
      case 3:
      case 7:
	  x1 -= TANK_DIAG;
	  x2 += TANK_DIAG;
	  y1 -= TANK_DIAG;
	  y2 += TANK_DIAG;
	  break;
  }

  return (b[1] >= x1) && (b[1] < x2) && (b[2] >= y1) && (b[2] < y2);
}


function moveBullet(b) {
  switch (b[0]) {
    case 0:
	b[2] -= SHOOT_INCR_RECT;
	break;
    case 1:
	b[1] += SHOOT_INCR_DIAG;
	b[2] -= SHOOT_INCR_DIAG;
	break;
    case 2:
	b[1] += SHOOT_INCR_RECT;
	break;
    case 3:
	b[1] += SHOOT_INCR_DIAG;
	b[2] += SHOOT_INCR_DIAG;
	break;
    case 4:
	b[2] += SHOOT_INCR_RECT;
	break;
    case 5:
	b[1] -= SHOOT_INCR_DIAG;
	b[2] += SHOOT_INCR_DIAG;
	break;
    case 6:
	b[1] -= SHOOT_INCR_RECT;
	break;
    case 7:
	b[1] -= SHOOT_INCR_DIAG;
	b[2] -= SHOOT_INCR_DIAG;
	break;
  }
  return (b[1] >= 0) && (b[2] >= 0) && (b[1] < ARENA_WIDTH) && (b[2] < ARENA_HEIGHT);
}

function shootBullets() {
  if (energy < BULLET_ENERGY) {return;}
  bullets.push(newBullet(tx, ty, orientation));
  bulletsFired++;
  energy -= BULLET_ENERGY;
}

function checkBullets() {
  var beg = true;
  for (var i in bullets) {
    var b = bullets[i];
    if (!moveBullet(b)) {if (beg) {bullets.shift();}}
    else if (collides(b, etx, ety)) {if (beg) {bullets.shift();}}
    else {beg = false;}
  }
  beg = true;
  for (var i in enemyBullets) {
    var b = enemyBullets[i];
    if (!moveBullet(b)) {if (beg) {enemyBullets.shift();}}
    else if (collides(b, tx, ty)) {
      if (beg) {enemyBullets.shift();}
      health -= BULLET_DAMAGE;
      if (health <= 0) {
	  //resetPos();
	  deaths++;
	  //exploded = true;
	  if (conn.readyState === 1) {
	    conn.send("EXPL");
	  }
	  explode();
      }
    } else {beg = false;}
  }
}
