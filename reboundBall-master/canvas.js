//原型 和方形物体检测碰撞

$(function () {
    var canvas = $("#canvas");
	 
// 计算两点之间的距离
var  DistanceBetweenTwoPoints=function(  x1,   y1,   x2,   y2)
{
	return Math.sqrt((x2 - x1) * (x2 - x1) + (y2 - y1) * (y2 - y1));
}
 
// 计算点(x, y)到经过两点(x1, y1)和(x2, y2)的直线的距离
var  DistanceFromPointToLine=function(   x,   y,   x1,   y1,   x2,   y2)
{
	var a = y2 - y1;
	var b = x1 - x2;
	var c = x2 * y1 - x1 * y2; 
 
	return Math.abs(a * x + b * y + c) / Math.sqrt(a * a + b * b);
}
  

var IsCircleIntersectLineSeg=function (   x,    y,    r,    x1,    y1,    x2,    y2)
{
	var  vx1 = x - x1;
	var  vy1 = y - y1;
	var  vx2 = x2 - x1;
	var  vy2 = y2 - y1;
 
	//assert(fabs(vx2) > 0.00001f || fabs(vy2) > 0.00001f);
 
	// len = v2.length()
	var  len = Math.sqrt(vx2 * vx2 + vy2 * vy2);
 
	// v2.normalize()
	vx2 /= len;
	vy2 /= len;
 
	// u = v1.dot(v2)
	// u is the vector projection length of vector v1 onto vector v2.
	var  u = vx1 * vx2 + vy1 * vy2;
 
	// determine the nearest point on the lineseg
	var  x0 = 0;
	var  y0 = 0;
	if (u <= 0)
	{
		// p is on the left of p1, so p1 is the nearest point on lineseg
		x0 = x1;
		y0 = y1;
	}
	else if (u >= len)
	{
		// p is on the right of p2, so p2 is the nearest point on lineseg
		x0 = x2;
		y0 = y2;
	}
	else
	{
		// p0 = p1 + v2 * u
		// note that v2 is already normalized.
		x0 = x1 + vx2 * u;
		y0 = y1 + vy2 * u;
	}
 
	return (x - x0) * (x - x0) + (y - y0) * (y - y0) <= r * r;
}


var p0x=100;
var p0y=100;
 
 var p1x=200;
 var p1y=100;
var px=(p1x-p0x)/2+p0x;
var py=(p1y-p0y)/2+p0y;  
	
	 canvas.onmousedown = function(ev){
                var ev=ev || window.event;
                ogc.moveTo(ev.clientX-oc.offsetLeft,ev.clientY-oc.offsetTop);
                document.onmousemove = function(ev){
                    var ev = ev || window.event;
                    ogc.lineTo(ev.clientX-oc.offsetLeft,ev.clientY-oc.offsetTop);
                    ogc.stroke();
                };
                document.onmouseup = function(){
                    document.onmousemove = null;
                    document.onmouseup = null;
                }; 
	}
	var clickX = new Array();
var clickY = new Array();
	var clickX_temp = new Array();
var clickY_temp = new Array();
var clickDrag = new Array();
var paint;
 function redraw(){
  canvas.width = canvas.width; // Clears the canvas
 
  context.strokeStyle = "#df4b26";
  context.lineJoin = "round";
  context.lineWidth = 5; 
if(!paint){
	//结束
	clickX_temp = clickX;
	clickY_temp = clickY;
}
  for(var i=0; i < clickX.length; i++)
  {
    context.beginPath();
	
	
	
    if(clickDrag[i] && i){//当是拖动而且i!=0时，从上一个点开始画线。
      context.moveTo(clickX_temp[i-1], clickY_temp[i-1]);
	  
	//   p0x=clickX_temp[i-1];
	//p0y=clickY_temp[i-1];
	  
     }else{
       context.moveTo(clickX_temp[i]-1, clickY_temp[i]);
	//	  p0x=clickX_temp[i]-1;
	//	p0y=clickY_temp[i];
     }
     context.lineTo(clickX_temp[i], clickY_temp[i]);
	//   p1x=clickX_temp[i];
	//	p1y=clickY_temp[i];
	 
//	balls[0].x:223.724492418075,balls[0].y:155.10817942559095,balls[0].radius:24.98872137953699
//canvas.js:117 p0x:145,p0y:223,p1x:518,p1y:141

	
	 
	 
	 
	 
	 
	 
     context.closePath();
     context.stroke();
  }
}
function addClick(x, y, dragging)
{
  clickX.push(x);
  clickY.push(y);
  clickDrag.push(dragging);
}
$('#canvas').mouseleave(function(e){
  paint = false;
  addClick(e.pageX - this.offsetLeft, e.pageY - this.offsetTop, true);
});
$('#canvas').mouseup(function(e){
  paint = false;
  addClick(e.pageX - this.offsetLeft, e.pageY - this.offsetTop, true);
});


$('#canvas').mousemove(function(e){
  if(paint){//是不是按下了鼠标
   // addClick(e.pageX - this.offsetLeft, e.pageY - this.offsetTop, true);
   // redraw();
  }
});
	
	$('#canvas').mousedown(function(e){
	  var mouseX = e.pageX - this.offsetLeft;
	  var mouseY = e.pageY - this.offsetTop;
	 
  clickX = new Array();
  clickY = new Array();
	  paint = true;
	  addClick(e.pageX - this.offsetLeft, e.pageY - this.offsetTop);
	  //redraw();
	});
	
    var context = canvas.get(0).getContext("2d");
    var canvasWidth = canvas.width();
    var canvasHeight = canvas.height();
    var numBall = 1;
    var colors = ["#FFB6C1", "#e2ff5b", "#FF4500", "#9ACD32", "#7FFFD4", "#ff6900", "#ddff59", "#66FFCC", "#00FF99"];
    var flag = true;
    var Ball = function (radius, vx, vy, color) {
        this.x = 100;
        this.y = 150;
        this.radius = radius;
        this.vx = vx;
        this.vy = vy;
        this.color = color;
		this.draw = function  (context) {
          context.fillStyle = this.color;
            context.beginPath();
            context.arc(this.x, this.y, this.radius, 0, Math.PI * 2, true);
            context.closePath();
            context.fill();
        };
		 
		
    };
	//  
	var Line = function (radius, x1, y1,x2,y2, color) {
		this.type="Line"; 
        this.color = color;
		 
		this.draw = function  (ctx) {
			//console.log("Line:");		
	ctx.beginPath();
    ctx.moveTo(x1, y1);
    ctx.lineTo(x2, y2);  
    ctx.fillStyle = "#FF0000";
    ctx.strokeStyle = "#FF0000";
    ctx.stroke(); 
					
			 		 var p0x=x1;
			 	var p0y=y1;
			 	 
			 	 var p1x=x2;
			 	 var p1y=y2;
			 	 
			 	 var isIntersect=IsCircleIntersectLineSeg(balls[0].x,balls[0].y,balls[0].radius, p0x,p0y,p1x,p1y)
			 	 
			 	 if(isIntersect){
			 			console.log("isIntersect:"+isIntersect);
			 	 }
        };
    };
	
function getRect(obj){
		return obj.getBoundingClientRect();
	}
function collision(obj1,obj2){
	var obj1Rect = 	getRect(obj1);
	var obj2Rect = 	getRect(obj2);
 
	//如果obj1碰上了哦obj2返回true，否则放回false
	var obj1Left = obj1Rect.left;
	var obj1Right = obj1Rect.right;
	var obj1Top = obj1Rect.top;
	var obj1Bottom = obj1Rect.bottom;
 
	var obj2Left = obj2Rect.left;
	var obj2Right = obj2Rect.right;
	var obj2Top = obj2Rect.top;
	var obj2Bottom = obj2Rect.bottom;
 
	if( obj1Right < obj2Left || obj1Left > obj2Right || obj1Bottom < obj2Top || obj1Top > obj2Bottom ){
		console.log("false");
		return false;
	}else{
		console.log("true");
		flag=false;
		return true;
	}
}
	
	 
	var Rect = function (radius, vx, vy, color) {
		this.type="Rect";
        this.x = 100;
        this.y = 100; 
        this.width = 100; 
        this.height = 30; 
        this.vx = vx;
        this.vy = vy;
        this.color = color;
		this.radius=20;
		this.draw = function  (ctx1) {
            ctx1.save(); 
			ctx1.fillStyle='blue';
            ctx1.translate((this.x+(this.width/2)),(this.y+(this.height/2)));
            ctx1.rotate(20*Math.PI/180);
            ctx1.strokeRect(this.x,this.y,this.width,this.height);
            ctx1.fillRect(this.x,this.y,this.width,this.height);
           
			ctx1.restore();
			
		

        };
    };

    var balls=[];
    for (var i = 0; i < numBall; i++) {
        var radius = 15 + (Math.random() * 10);
        var vx = 100;//(Math.random() * 200) - 10;
        var vy = 0;//(Math.random() * 200) - 10;
        var index = Math.floor(Math.random() * 10);
        var color = colors[index];
        var ball = new Ball(radius, vx, vy, color);
        balls.push(ball);       //向数组的末尾添加
    }
	
	 
		 var vx = (Math.random() * 20) - 10;
			var vy = (Math.random() * 20) - 10;
		 var index = Math.floor(Math.random() * 10);
			var color = colors[index];
		var ballR = new Rect(radius, vx, vy, color);
			balls.push(ballR);
	 
	var Line1 = new Line(radius, 100,100,250,200, color);
	balls.push(Line1);
	 
	 
var ball=balls[0];
var cv = {r:ball.radius,p0:{x:ball.x,y:ball.y},vx:ball.vx, vy:ball.vy};
 	 
var walls = [{p0:{x:100,y:100}, p1:{x:250,y:200}, bf:1, wf:1},
{p0:{x:0,y:0}, p1:{x:400,y:0}, bf:1, wf:1},
              {p0:{x:400,y:0}, p1:{x:400,y:220}, bf:1, wf:1},
              {p0:{x:400,y:220}, p1:{x:0,y:220}, bf:1, wf:1},
              {p0:{x:0,y:220}, p1:{x:0,y:0}, bf:1, wf:1}
 ];

for(var i = 0; i < walls.length; ++i) {
    updateVector(walls[i], true);
}

	 var lastTime = new Date().getTime();
	 
    function animate() {
        context.clearRect(0, 0, canvasWidth, canvasHeight);      //清空
        for (var i = 0; i < balls.length; i++) {
            var tmpball;
            tmpball = balls[i];
         //   if (tmpball.x + tmpball.radius > canvasWidth || tmpball.x - tmpball.radius < 0) {
         //       tmpball.vx = -tmpball.vx;
         //   }
         //   if (tmpball.y + tmpball.radius > canvasHeight || tmpball.y - tmpball.radius < 0) {
         //       tmpball.vy = -tmpball.vy;
         //   }
          
			if(tmpball["type"]=="Rect"){
				 context.fillStyle = "steelblue";
				tmpball.draw(context);
				  
			}else{  //检测边界碰撞
				//tmpball.x += tmpball.vx;
				//tmpball.y += tmpball.vy; 
				tmpball.draw(context);
			}
			
        }
		
 
//球所在位置
 var circle=balls[0];
    var nowTime = new Date().getTime();
    var delta = (nowTime - lastTime) / 1000;
    lastTime = nowTime; 
    updateObjPosByTime(cv, delta);
    checkWalls(cv, walls);
    circle.x = cv.p1.x;
    circle.y = cv.p1.y;
    cv.p0 = cv.p1;
    cv.vx /= cv.delta;
    cv.vy /= cv.delta; 
		//collision(balls[0],balls[1]);
		//redraw();
        if (flag) {
            requestAnimationFrame(animate);     //执行动画
        }
    }


    $("button").click(function () {
        flag = flag ? false : true;
        //按下按钮改变状态
        if (flag) {
            animate();
        }
    });
    function show() {
        
        animate();
	   
    }
	function rotate2(ctx1,obj){
           
            ctx1.fillStyle='blue';
            ctx1.translate((obj.x+(obj.width/2)),(obj.y+(obj.height/2)));
            ctx1.rotate(20*Math.PI/180);
            ctx1.strokeRect(obj.x,obj.y,obj.width,obj.height);
            ctx1.fillRect(obj.x,obj.y,obj.width,obj.height);
            ctx1.translate(-(obj.x+(obj.width/2)),-(obj.y+(obj.height/2)));
			//ctx1.rotate(-20*Math.PI/180);
        }
     show();
});