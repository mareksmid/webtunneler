"use strict";

var WebTunneler = function(boardId, explodeId, newGame, gameId, server) {
    this.newGame = newGame;
    this.gameId = gameId;
    this.server = server;
    this.conn = {};
    this.board = document.getElementById(boardId);
    this.ctx = board.getContext('2d');

    this.orientation = 0;
    this.tx = 0; this.ty = 0;
    this.ax = 0; this.ay = 0;
    this.bx = 0; this.by = 0;
    this.rx = 0; this.ry = 0;
    this.energy = Consts.MAX_ENERGY;
    this.health = Consts.MAX_HEALTH;
    this.shooting = false;
    this.bullets = {};
    this.newBullets = [];
    this.stones = null;
    this.dirt = null;

    this.eorientation = -1;
    this.etx = 0; this.ety = 0;
    this.ebx = 0; this.eby = 0;
    this.enemyBullets = {};

    this.deaths = 0; this.enemyDeaths = 0;

    this.pressed = 0;
    this.timer = null;
    this.shootCtr = 0;
    this.bulletId = 0;
    this.started = false;

    this.tankImg = new Image();
    this.enemyTankImg = new Image();
    this.tankImg.src= '/wt/img/tank.png';
    this.enemyTankImg.src= '/wt/img/enemytank.png';
    this.explAudio = document.getElementById(explodeId);

    if (window.WebSocket === undefined) {
        this.print('WebSockets not supported');
    } else {
        this.print('Waiting for start...');
        this.openConnection();
    }
};

WebTunneler.prototype.openConnection = function() {
    if (this.conn.readyState === undefined || conn.readyState > 1) {
        this.conn = new WebSocket('ws://'+this.server+'/wt/wts');
        this.conn.onopen = function () {
            console.log('Socket open');
            var cmd = this.newGame ? 'NEW' : 'JOIN';
            this.sendInit(this.gameId, cmd);
        }.bind(this);

        this.conn.onmessage = this.processPacket.bind(this);

        this.conn.onclose = function (event) {
            console.log('Socket closed');
        };
    }
};

WebTunneler.prototype.processPacket = function(event) {
    var data = JSON.parse(event.data);
    if (data.cmd == 'SCENE') {
        this.initBoard(data);
        return;
    }
    if (data.cmd == 'EEXPL') {
        this.enemyDeaths++;
        this.explAudio.play();
        return;
    }
    if (data.cmd == 'EXPL') {
        this.deaths++;
        this.explAudio.play();
        return;
    }
    this.tx = this.rx = data.x;
    this.ty = this.ry = data.y;
    this.energy = data.e;
    this.health = data.h;

    this.eorientation = data.eor;
    this.etx = data.ex;
    this.ety = data.ey;
    //enemyBullets = enemyBullets.concat(data.eb);
    for (var i in data.eb) {
        var b = data.eb[i];
        this.enemyBullets[b.id] = b;
    }

    for (var di in data.drem) {
        var d = data.drem[di];
        this.dirt[d.x][d.y] = false;
    }

    for (var bi in data.brem) {
        delete this.bullets[data.brem[bi]];
    }
    for (var ebi in data.ebrem) {
        delete this.enemyBullets[data.ebrem[ebi]];
    }
};

WebTunneler.prototype.doTimer = function() {
    this.updatePos();

    this.checkBullets();
    if (this.shooting) {
        if (this.shootCtr == 0) {
            this.shootBullets();
            this.shootCtr = Consts.SHOOT_DIV;
        } else {
            this.shootCtr--;
        }
    }

    this.sendPos();
    this.draw();
};

WebTunneler.prototype.initBoard = function(data) {
    this.bx = data.bx;
    this.by = data.by;
    this.tx = this.rx = data.tx;
    this.ty = this.ry = data.ty;
    this.etx = data.etx;
    this.ety = data.ety;

    this.ebx = data.ebx;
    this.eby = data.eby;

    this.health = Consts.MAX_HEALTH;
    this.energy = Consts.MAX_ENERGY;

    this.stones = data.stones;

    this.dirt = [];
    for (var x = 0; x < Consts.DIRT_X_CNT; x++) {
        this.dirt[x] = [];
        for (var y = 0; y < Consts.DIRT_Y_CNT; y++) {
            this.dirt[x][y] = true;
        }
    }

    if (!this.started) {
        this.timer = setInterval(this.doTimer.bind(this), Consts.TIMER_INT);
        this.started = true;
    }
};

WebTunneler.prototype.updatePos = function() {
    switch (this.pressed) {
        case 0:
            break;
        case 1:
            this.orientation = 6;
            this.tx -= Consts.INCR_RECT;
            this.tx -= Consts.INCR_RECT;
            break;
        case 2:
            this.orientation = 0;
            this.ty -= Consts.INCR_RECT;
            break;
        case 3:
            this.orientation = 7;
            this.tx -= Consts.INCR_DIAG; this.ty -= Consts.INCR_DIAG;
            break;
        case 4:
            this.orientation = 2;
            this.tx += Consts.INCR_RECT;
            break;
        case 6:
            this.orientation = 1;
            this.tx += Consts.INCR_DIAG; this.ty -= Consts.INCR_DIAG;
            break;
        case 8:
            this.orientation = 4;
            this.ty += Consts.INCR_RECT;
            break;
        case 9:
            this.orientation = 5;
            this.tx -= Consts.INCR_DIAG; this.ty += Consts.INCR_DIAG;
            break;
        case 12:
            this.orientation = 3;
            this.tx += Consts.INCR_DIAG; this.ty += Consts.INCR_DIAG;
            break;
    }
    if (this.tx < Consts.TANK_R) {this.tx = Consts.TANK_R;} else if (this.tx > Consts.ARENA_WIDTH-Consts.TANK_R) {this.tx = Consts.ARENA_WIDTH-Consts.TANK_R;}
    if (this.ty < Consts.TANK_R) {this.ty = Consts.TANK_R;} else if (this.ty > Consts.ARENA_HEIGHT-Consts.TANK_R) {this.ty = Consts.ARENA_HEIGHT-Consts.TANK_R;}
};


WebTunneler.prototype.sendInit = function(id, cmd) {
    var s = JSON.stringify({id:id, cmd:cmd});
    console.log('sending init: '+s);
    var res = this.conn.send(s);
    console.log('sent init: '+res);
};

WebTunneler.prototype.sendPos = function() {
    if (this.conn.readyState !== 1) {return;}
    this.conn.send(JSON.stringify({or:this.orientation, x:this.tx, y:this.ty, b:this.newBullets}));
    this.newBullets = [];
};


WebTunneler.prototype.keyDown = function(e) {
    var c = e.keyCode;
    if ((c >= 37) && (c <= 40)) {
        this.pressed |= 1 << (c-37);
    } else if (c == 17) {
        if (!this.shooting) {this.shootCtr = 0;}
        this.shooting = true;
    }
};

WebTunneler.prototype.keyUp = function(e) {
    var c = e.keyCode;
    if ((c >= 37) && (c <= 40)) {
        this.pressed &= ~(1 << (c-37));
    } else if (c == 17) {
        this.shooting = false;
    }
};

WebTunneler.prototype.print = function(text) {
    this.ctx.fillStyle = "#FF0000";
    this.ctx.fillText(text, 10, 20);
};
