
function draw() {
  ctx.fillStyle = "#E0E0E0";
  ctx.fillRect(0, 0, BOARD_WIDTH, BOARD_HEIGHT);
  ctx.fillStyle = "#00FF00";
  ctx.fillText(""+pressed, 10, 420);
  ctx.fillText(""+orientation+" "+tx+" "+ty, 10, 435);
  ctx.fillText(""+eorientation+" "+etx+" "+ety, 10, 450);
  ctx.fillText(""+energy+" "+health+" "+shooting+" "+bullets, 10, 465);
 
  ax = tx - BOARD_WIDTH/2;
  if (ax < 0) {ax = 0;} else if (ax > ARENA_WIDTH-BOARD_WIDTH+1) {ax = ARENA_WIDTH-BOARD_WIDTH+1;}
  ay = ty - BOARD_HEIGHT/2;
  if (ay < 0) {ay = 0;} else if (ay > ARENA_HEIGHT-BOARD_HEIGHT+1) {ay = ARENA_HEIGHT-BOARD_HEIGHT+1;}

  ctx.fillStyle = "#E0FFE0";
  ctx.fillRect(bx - ax, by - ay, BASE_WIDTH, BASE_HEIGHT);

  ctx.fillStyle = "#FFE0E0";
  ctx.fillRect(ebx - ax, eby - ay, BASE_WIDTH, BASE_HEIGHT);
  
  ctx.lineWidth = 4;
  ctx.strokeStyle = "#0000FF";
  ctx.strokeRect(-ax, -ay, ARENA_WIDTH, ARENA_HEIGHT);
  ctx.lineWidth = 1;

  ctx.translate(tx-ax, ty-ay);
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
