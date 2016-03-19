"use strict";

WebTunneler.prototype.newBullet = function(cx, cy, orientation) {
	var posX = cx, posY = cy;
	switch (orientation) {
		case 0:
			posY -= Consts.TANK_H2;
			break;
		case 1:
			posX += Consts.TANK_DIAG;
			posY -= Consts.TANK_DIAG;
			break;
		case 2:
			posX += Consts.TANK_H2;
			break;
		case 3:
			posX += Consts.TANK_DIAG;
			posY += Consts.TANK_DIAG;
			break;
		case 4:
			posY += Consts.TANK_H2;
			break;
		case 5:
			posX -= Consts.TANK_DIAG;
			posY += Consts.TANK_DIAG;
			break;
		case 6:
			posX -= Consts.TANK_H2;
			break;
		case 7:
			posX -= Consts.TANK_DIAG;
			posY -= Consts.TANK_DIAG;
			break;
	}
	return {id: (this.bulletId++), or: orientation, x: posX, y: posY};
};

WebTunneler.prototype.moveBullet = function(b) {
	switch (b.or) {
		case 0:
			b.y -= Consts.SHOOT_INCR_RECT;
			break;
		case 1:
			b.x += Consts.SHOOT_INCR_DIAG;
			b.y -= Consts.SHOOT_INCR_DIAG;
			break;
		case 2:
			b.x += Consts.SHOOT_INCR_RECT;
			break;
		case 3:
			b.x += Consts.SHOOT_INCR_DIAG;
			b.y += Consts.SHOOT_INCR_DIAG;
			break;
		case 4:
			b.y += Consts.SHOOT_INCR_RECT;
			break;
		case 5:
			b.x -= Consts.SHOOT_INCR_DIAG;
			b.y += Consts.SHOOT_INCR_DIAG;
			break;
		case 6:
			b.x -= Consts.SHOOT_INCR_RECT;
			break;
		case 7:
			b.x -= Consts.SHOOT_INCR_DIAG;
			b.y -= Consts.SHOOT_INCR_DIAG;
			break;
	}
	return (b.x >= 0) && (b.y >= 0) && (b.x < Consts.ARENA_WIDTH) && (b.y < Consts.ARENA_HEIGHT);
};

WebTunneler.prototype.shootBullets = function() {
	if (this.energy < Consts.BULLET_ENERGY) {return;}
	var b = this.newBullet(this.rx, this.ry, this.orientation);
	//bullets.push(b);
	this.newBullets.push(b);
	this.bullets[b.id] = b;
};

WebTunneler.prototype.checkBullets = function() {
	for (var i in this.bullets) {
		var b = this.bullets[i];
		if (!this.moveBullet(b)) {delete this.bullets[i];}
	}
	for (var i in this.enemyBullets) {
		var b = this.enemyBullets[i];
		if (!this.moveBullet(b)) {delete this.enemyBullets[i];}
	}
};
