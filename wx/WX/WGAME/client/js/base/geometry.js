/**
 * 几何类画图
 */
export default class Geometry {
 
  //参数解释：x1(2、3),y1(2、3)-三角形的三个点的坐标；color-绘制颜色；type-绘制类型（'fill'和'stroke'）。
  drawTriangle(ctx, x1, y1, x2, y2, x3, y3, color, type) {
    ctx.beginPath();
    ctx.moveTo(x1, y1);
    ctx.lineTo(x2, y2);
    ctx.lineTo(x3, y3);
    ctx.closePath();
    ctx.fillStyle = color;
    ctx.strokeStyle = "#FF0000";
    ctx.stroke();
    ctx.fill();
    // 
    //ctx[type]();
  }
  //绘制圆形 x,y-圆心；r 半径 start-起始角度；end-结束角度；cid-绘制颜色；type-绘制类型（'fill'和'stroke'） 
  drawCircle(ctx, x, y, r, start, end, cid, type) {
    var unit = Math.PI / 180;
    ctx.beginPath();
    ctx.arc(x, y, r, start * unit, end * unit);
    var color = mapColor(parseInt(cid));
    ctx[type + 'Style'] = color;
    ctx.closePath();
    ctx[type]();
  }

  drawPolygon(ctx, conf) {
    var x = conf && conf.x || 0; //中心点x坐标
    var y = conf && conf.y || 0; //中心点y坐标
    var num = conf && conf.num || 3; //图形边的个数
    var r = conf && conf.r || 100; //图形的半径
    var width = conf && conf.width || 5;
    var strokeStyle = conf && conf.strokeStyle;
    var fillStyle = conf && conf.fillStyle;
    //开始路径
    ctx.beginPath();
    var startX = x + r * Math.cos(2 * Math.PI * 0 / num);
    var startY = y + r * Math.sin(2 * Math.PI * 0 / num);
    ctx.moveTo(startX, startY);
    for (var i = 1; i <= num; i++) {
      var newX = x + r * Math.cos(2 * Math.PI * i / num);
      var newY = y + r * Math.sin(2 * Math.PI * i / num);
      ctx.lineTo(newX, newY);
    }
    ctx.closePath();
    //路径闭合
    if (strokeStyle) {
      ctx.strokeStyle = strokeStyle;
      ctx.lineWidth = width;
      ctx.lineJoin = 'round';
      ctx.stroke();
    }
    if (fillStyle) {
      ctx.fillStyle = fillStyle;
      ctx.fill();
    }
  }
  //绘制（圆角）矩形
  //参数解释：x,y-左上角点的坐标；width、height-宽高；radius-圆角；color-绘制颜色；type-绘制类型（'fill'和'stroke'）。
  drawCircleRect(ctx, x, y, width, height, radius, color, type) {
    ctx.beginPath();
    ctx.moveTo(x, y + radius);
    ctx.lineTo(x, y + height - radius);
    ctx.quadraticCurveTo(x, y + height, x + radius, y + height);
    ctx.lineTo(x + width - radius, y + height);
    ctx.quadraticCurveTo(x + width, y + height, x + width, y + height - radius);
    ctx.lineTo(x + width, y + radius);
    ctx.quadraticCurveTo(x + width, y, x + width - radius, y);
    ctx.lineTo(x + radius, y);
    ctx.quadraticCurveTo(x, y, x, y + radius);


    ctx[type + 'Style'] = color;
    ctx.closePath();
    ctx[type]();
  }

}