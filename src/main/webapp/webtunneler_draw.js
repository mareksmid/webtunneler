"use strict";

WebTunneler.prototype.draw = function() {
    var ctx = this.ctx;
    ctx.fillStyle = "#E0E0E0";
    ctx.fillRect(0, 0, Consts.BOARD_WIDTH, Consts.BOARD_HEIGHT);

    this.ax = this.rx - Consts.BOARD_WIDTH/2;
    if (this.ax < 0) {this.ax = 0;} else if (this.ax > Consts.ARENA_WIDTH-Consts.BOARD_WIDTH+1) {this.ax = Consts.ARENA_WIDTH-Consts.BOARD_WIDTH+1;}
    this.ay = this.ry - Consts.BOARD_HEIGHT/2;
    if (this.ay < 0) {this.ay = 0;} else if (this.ay > Consts.ARENA_HEIGHT-Consts.BOARD_HEIGHT+1) {this.ay = Consts.ARENA_HEIGHT-Consts.BOARD_HEIGHT+1;}

    if (this.dirt != null) {
        ctx.fillStyle = "#666600";
        for (var x = 0; x < Consts.DIRT_X_CNT; x++) for (var y = 0; y < Consts.DIRT_Y_CNT; y++) {
            if (this.dirt[x][y]) {ctx.fillRect(x*Consts.DIRT_W - this.ax, y*Consts.DIRT_H - this.ay, Consts.DIRT_W, Consts.DIRT_H);}
        }
    }

    if (this.stones != null) {
        ctx.fillStyle = '#404040';
        for (var si in this.stones) {
            var s = this.stones[si];
            ctx.beginPath();
            ctx.moveTo(s.points[0].x-this.ax, s.points[0].y-this.ay);
            for (var i = 1; i < s.points.length-1; i++) {ctx.lineTo(s.points[i].x-this.ax, s.points[i].y-this.ay);}
            ctx.closePath();
            ctx.fill();
        }
    }

    this.drawBase("#E0FFE0", this.bx, this.by);
    this.drawBase("#FFE0E0", this.ebx, this.eby);

    ctx.lineWidth = 4;
    ctx.strokeStyle = "#0000FF";
    ctx.strokeRect(-this.ax, -this.ay, Consts.ARENA_WIDTH, Consts.ARENA_HEIGHT);
    ctx.lineWidth = 1;

    ctx.translate(this.rx-this.ax, this.ry-this.ay);
    ctx.rotate(Math.PI * 2 * this.orientation / 8);
    ctx.drawImage(this.tankImg, -Consts.TANK_W2, -Consts.TANK_H2);
    ctx.setTransform(1, 0, 0, 1, 0, 0);

    if (this.eorientation >= 0) {
        ctx.translate(this.etx-this.ax, this.ety-this.ay);
        ctx.rotate(Math.PI * 2 * this.eorientation / 8);
        ctx.drawImage(this.enemyTankImg, -Consts.TANK_W2, -Consts.TANK_H2);
        ctx.setTransform(1, 0, 0, 1, 0, 0);
    }

    ctx.strokeStyle = "#FF0000";
    for (var i in this.bullets) {
        var b = this.bullets[i];
        ctx.strokeRect(b.x-this.ax-2, b.y-this.ay-2, 4, 4);
    }
    for (var i in this.enemyBullets) {
        var b = this.enemyBullets[i];
        ctx.strokeRect(b.x-this.ax-2, b.y-this.ay-2, 4, 4);
    }

    //if (eorientation >= 0) {
    var hlo = Consts.BOARD_WIDTH/3, hw = Consts.BOARD_WIDTH/3;
    ctx.strokeStyle = "#FF0000";
    ctx.strokeRect(hlo, Consts.BOARD_HEIGHT - Consts.HEALTH_BOT_OFFS, hw - 1, Consts.HEALTH_WIDTH - 1);
    ctx.fillStyle = "rgba(255,0,0,0.5)";
    ctx.fillRect(hlo + 1, Consts.BOARD_HEIGHT - Consts.HEALTH_BOT_OFFS + 1, (hw - 2) * this.health / Consts.MAX_HEALTH,  Consts.HEALTH_WIDTH - 2);

    ctx.strokeStyle = "#0000FF";
    ctx.strokeRect(hlo, Consts.BOARD_HEIGHT - Consts.ENERGY_BOT_OFFS, hw - 1, Consts.ENERGY_WIDTH - 1);
    ctx.fillStyle = "rgba(0,0,255,0.5)";
    ctx.fillRect(hlo + 1, Consts.BOARD_HEIGHT - Consts.ENERGY_BOT_OFFS + 1, (hw - 2) * this.energy / Consts.MAX_ENERGY,  Consts.ENERGY_WIDTH - 2);

    ctx.fillStyle = "#008000";
    //g.setFont(SCORE_FONT);
    var sx = Consts.BOARD_WIDTH/2 - Consts.BOARD_WIDTH/6;
    ctx.fillText(""+this.enemyDeaths, sx, Consts.SCORE_T_OFFS);
    ctx.fillStyle = "#FF0000";
    sx = Consts.BOARD_WIDTH/2 + Consts.BOARD_WIDTH/6;
    ctx.fillText(""+this.deaths, sx, Consts.SCORE_T_OFFS);
    //}

    ctx.fillStyle = "#00FF00";
    ctx.fillText(""+this.pressed, 10, 420);
    ctx.fillText(""+this.orientation+" "+this.tx+" "+this.ty+" / "+this.rx+" "+this.ry, 10, 435);
    ctx.fillText(""+this.eorientation+" "+this.etx+" "+this.ety, 10, 450);
    ctx.fillText(""+this.energy+" "+this.health+" "+this.shooting, 10, 465);
};

WebTunneler.prototype.drawBase = function(bcl, bx, by) {
    var ctx = this.ctx;

    ctx.fillStyle = bcl;
    ctx.fillRect(bx - this.ax, by - this.ay, Consts.BASE_WIDTH, Consts.BASE_HEIGHT);

    ctx.lineWidth = 2;
    ctx.strokeStyle = "#FF0000";
    ctx.beginPath();
    ctx.moveTo(bx - this.ax + (Consts.BASE_WIDTH-Consts.BASE_ENTRANCE)/2, by - this.ay);
    ctx.lineTo(bx - this.ax, by - this.ay);
    ctx.lineTo(bx - this.ax, by - this.ay + Consts.BASE_HEIGHT);
    ctx.lineTo(bx - this.ax + (Consts.BASE_WIDTH-Consts.BASE_ENTRANCE)/2, by - this.ay + Consts.BASE_HEIGHT);
    ctx.stroke();

    ctx.beginPath();
    ctx.moveTo(bx - this.ax + Consts.BASE_WIDTH - (Consts.BASE_WIDTH-Consts.BASE_ENTRANCE)/2, by - this.ay);
    ctx.lineTo(bx - this.ax + Consts.BASE_WIDTH, by - this.ay);
    ctx.lineTo(bx - this.ax + Consts.BASE_WIDTH, by - this.ay + Consts.BASE_HEIGHT);
    ctx.lineTo(bx - this.ax + Consts.BASE_WIDTH - (Consts.BASE_WIDTH-Consts.BASE_ENTRANCE)/2, by - this.ay + Consts.BASE_HEIGHT);
    ctx.stroke();
};
