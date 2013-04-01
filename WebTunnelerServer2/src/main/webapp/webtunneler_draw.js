
function draw() {
  ctx.fillStyle = "#E0E0E0";
  ctx.fillRect(0, 0, BOARD_WIDTH, BOARD_HEIGHT);
  ctx.fillStyle = "#00FF00";
  ctx.fillText(""+pressed, 10, 420);
  ctx.fillText(""+orientation+" "+tx+" "+ty+" / "+rx+" "+ry, 10, 435);
  ctx.fillText(""+eorientation+" "+etx+" "+ety, 10, 450);
  ctx.fillText(""+energy+" "+health+" "+shooting+" "+bullets, 10, 465);
  
  ax = rx - BOARD_WIDTH/2;
  if (ax < 0) {ax = 0;} else if (ax > ARENA_WIDTH-BOARD_WIDTH+1) {ax = ARENA_WIDTH-BOARD_WIDTH+1;}
  ay = ry - BOARD_HEIGHT/2;
  if (ay < 0) {ay = 0;} else if (ay > ARENA_HEIGHT-BOARD_HEIGHT+1) {ay = ARENA_HEIGHT-BOARD_HEIGHT+1;}
  
  if (dirt != null) {
    ctx.fillStyle = "#666600";
    for (var x = 0; x < DIRT_X_CNT; x++) for (var y = 0; y < DIRT_Y_CNT; y++) {
      if (dirt[x][y]) {ctx.fillRect(x*DIRT_W - ax, y*DIRT_H - ay, DIRT_W, DIRT_H);}
    }
  }

  if (stones != null) {
    ctx.fillStyle = '#404040';
    for (var si in stones) {
      var s = stones[si];
      ctx.beginPath();
      ctx.moveTo(s.points[0].x-ax, s.points[0].y-ay);
      for (var i = 1; i < s.points.length-1; i++) {ctx.lineTo(s.points[i].x-ax, s.points[i].y-ay);}
      ctx.closePath();
      ctx.fill();
    }
  }
  
  drawBase("#E0FFE0", bx, by);
  drawBase("#FFE0E0", ebx, eby);

  ctx.lineWidth = 4;
  ctx.strokeStyle = "#0000FF";
  ctx.strokeRect(-ax, -ay, ARENA_WIDTH, ARENA_HEIGHT);
  ctx.lineWidth = 1;

  ctx.translate(rx-ax, ry-ay);
  ctx.rotate(Math.PI * 2 * orientation / 8);
  ctx.drawImage(tankImg, -TANK_W2, -TANK_H2);
  ctx.setTransform(1, 0, 0, 1, 0, 0);

  if (eorientation >= 0) {
    ctx.translate(etx-ax, ety-ay);
    ctx.rotate(Math.PI * 2 * eorientation / 8);
    ctx.drawImage(enemyTankImg, -TANK_W2, -TANK_H2);
    ctx.setTransform(1, 0, 0, 1, 0, 0);
  }
  
  ctx.strokeStyle = "#FF0000";
  for (var i in bullets) {
    var b = bullets[i];
    ctx.strokeRect(b[1]-ax-2, b[2]-ay-2, 4, 4);
  }
  for (var i in enemyBullets) {
    var b = enemyBullets[i];
    ctx.strokeRect(b[1]-ax-2, b[2]-ay-2, 4, 4);
  }

  //if (eorientation >= 0) {
    var hlo = BOARD_WIDTH/3, hw = BOARD_WIDTH/3;
    ctx.strokeStyle = "#FF0000";
    ctx.strokeRect(hlo, BOARD_HEIGHT - HEALTH_BOT_OFFS, hw - 1, HEALTH_WIDTH - 1);
    ctx.fillStyle = "rgba(255,0,0,0.5)";
    ctx.fillRect(hlo + 1, BOARD_HEIGHT - HEALTH_BOT_OFFS + 1, (hw - 2) * health / MAX_HEALTH,  HEALTH_WIDTH - 2);

    ctx.strokeStyle = "#0000FF";
    ctx.strokeRect(hlo, BOARD_HEIGHT - ENERGY_BOT_OFFS, hw - 1, ENERGY_WIDTH - 1);
    ctx.fillStyle = "rgba(0,0,255,0.5)";
    ctx.fillRect(hlo + 1, BOARD_HEIGHT - ENERGY_BOT_OFFS + 1, (hw - 2) * energy / MAX_ENERGY,  ENERGY_WIDTH - 2);

    ctx.fillStyle = "#008000";
    //g.setFont(SCORE_FONT);
    var sx = BOARD_WIDTH/2 - BOARD_WIDTH/6;
    ctx.fillText(""+enemyDeaths, sx, SCORE_T_OFFS);
    ctx.fillStyle = "#FF0000";
    sx = BOARD_WIDTH/2 + BOARD_WIDTH/6;
    ctx.fillText(""+deaths, sx, SCORE_T_OFFS);
  //}
  
}

function drawBase(bcl, bx, by) {
    ctx.fillStyle = bcl;
    ctx.fillRect(bx - ax, by - ay, BASE_WIDTH, BASE_HEIGHT);

    ctx.lineWidth = 2;
    ctx.strokeStyle = "#FF0000";
    ctx.beginPath();
    ctx.moveTo(bx - ax + (BASE_WIDTH-BASE_ENTRANCE)/2, by - ay);
    ctx.lineTo(bx - ax, by - ay);
    ctx.lineTo(bx - ax, by - ay + BASE_HEIGHT);
    ctx.lineTo(bx - ax + (BASE_WIDTH-BASE_ENTRANCE)/2, by - ay + BASE_HEIGHT);
    ctx.stroke();

    ctx.beginPath();
    ctx.moveTo(bx - ax + BASE_WIDTH - (BASE_WIDTH-BASE_ENTRANCE)/2, by - ay);
    ctx.lineTo(bx - ax + BASE_WIDTH, by - ay);
    ctx.lineTo(bx - ax + BASE_WIDTH, by - ay + BASE_HEIGHT);
    ctx.lineTo(bx - ax + BASE_WIDTH - (BASE_WIDTH-BASE_ENTRANCE)/2, by - ay + BASE_HEIGHT);
    ctx.stroke();
}
