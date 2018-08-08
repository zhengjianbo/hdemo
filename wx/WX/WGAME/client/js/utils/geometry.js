/**
 * 几何类画图
 */
var config = require('../../config')
var per = config.layoutConfig.per;

//画线
var drawLine = (ctx, conf) => { 
  var x1 = conf && conf.p1.x || 0;
  var y1 = conf && conf.p1.y || 0;
  var x2 = conf && conf.p2.x || 0;
  var y2 = conf && conf.p2.y || 0; 
  var scolor = conf && conf.strokeColor || "#FFFFFF"; 

  var lineWidth = conf && conf.lineWidth || 1;
  ctx.lineWidth = lineWidth;
  ctx.beginPath();
  ctx.moveTo(x1 + per, y1 + per);
  ctx.lineTo(x2 + per, y2 + per); 
  ctx.strokeStyle = scolor;

  ctx.stroke();
  ctx.closePath();

}
//画方框
var drawRect = (ctx, conf) => {
  var x = conf && conf.x || 0;
  var y = conf && conf.y || 0;
  var width = conf && conf.width || 0;
  var height = conf && conf.height || 0;
  var angle = conf && conf.angle || 0;
  //原点 长宽  角度
  //画 区域 
  var unit = angle*Math.PI / 180;
  var point_next_x = x + width * Math.cos(unit);
  var point_next_y = y + width * Math.sin(unit) ;

  var unitx = (angle+90) * Math.PI / 180;
  var point_down_x = x + width * Math.cos(unitx);
  var point_down_y = y + width * Math.sin(unitx);



  drawLine(ctx, {
    strokeColor: "#00FF00",
    p1: {
      x: x + per,
      y: y + per,
    },
    p2: {
      x: point_next_x + per,
      y: point_next_y + per,
    },
    lineWidth: 1
  });

  drawLine(ctx, {
    strokeColor: "#00FF00",
    p1: {
      x: x + per,
      y: y + per,
    },
    p2: {
      x: point_down_x + per,
      y: point_down_y + per,
    },
    lineWidth: 1
  });


}


//三角形
var drawTriangle = (ctx, conf) => {
 
  var x1 = conf && conf.p1.x || 0; 
  var y1 = conf && conf.p1.y || 0;
  var x2= conf && conf.p2.x || 0;
  var y2 = conf && conf.p2.y || 0;
  var x3 = conf && conf.p3.x || 0;
  var y3 = conf && conf.p3.y || 0; 

  var color = conf && conf.fillColor || "#FFFFFF";
  var scolor = conf && conf.strokeColor || "#FFFFFF"; 
  ctx.beginPath();
  ctx.moveTo(x1, y1);
  ctx.lineTo(x2, y2);
  ctx.lineTo(x3, y3);

  ctx.closePath();
  ctx.fillStyle = color;
  ctx.strokeStyle = scolor;
  ctx.stroke();
  ctx.fill();

}

//画圆
var drawCircle = (ctx, conf) =>{
  var x = conf && conf.x || 0;
  var y = conf && conf.y || 0;
  var r = conf && conf.r || 0;
  var start = conf && conf.start || 0;
  var end = conf && conf.end || 0;
 
  var color = conf && conf.fillColor || "#FF0000";
  var scolor = conf && conf.strokeColor || "#FF0000"; 

  var unit = Math.PI / 180;
  ctx.beginPath();
  ctx.arc(x, y, r, start * unit, end * unit);
  ctx.closePath();
  ctx.fillStyle = color;
  ctx.strokeStyle = scolor;
  ctx.fill();
  ctx.stroke();
    
}

//多边形 
var drawPolygon= (ctx, conf) => {
  var x = conf && conf.x || 0;  //中心点x坐标
  var y = conf && conf.y || 0;  //中心点y坐标
  var num = conf && conf.num || 3;   //图形边的个数
  var r = conf && conf.r || 100;   //图形的半径
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

// 功能类型
module.exports = { drawTriangle, drawPolygon, drawLine, drawCircle,drawRect}
 