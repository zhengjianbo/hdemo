import Sprite from '../base/sprite'

const screenWidth  = window.innerWidth
const screenHeight = window.innerHeight

 const BG_IMG_SRC   = 'images/bg.jpg'
const BG_WIDTH     = 512
const BG_HEIGHT    = 512

 
export default class Game extends Sprite {
  constructor(ctx) {
    super(BG_IMG_SRC, BG_WIDTH, BG_HEIGHT)
 
 
   // this.render(ctx)

  }
//定时更新
  update() {
    // this.top += 2 
    // if ( this.top >= screenHeight )
    //   this.top = 0
  }
 
  render(context) {
     
    context.beginPath();
    context.fillStyle  = "#FFFFFF";
    context.fillRect(0, 0, screenWidth, screenHeight)

    context.beginPath();
    context.moveTo(10, 10);
    context.lineTo(200, 200);
    //设置样式
    context.lineWidth = 2;
    context.strokeStyle = "#0000FF";
    //绘制
    context.stroke();


    // context.beginPath();
    // //设置是个顶点的坐标，根据顶点制定路径
    // for (var i = 0; i < 5; i++) {
    //   context.lineTo(Math.cos((18 + i * 72) / 180 * Math.PI) * 200 + 200,
    //     -Math.sin((18 + i * 72) / 180 * Math.PI) * 200 + 200);
    //   context.lineTo(Math.cos((54 + i * 72) / 180 * Math.PI) * 80 + 200,
    //     -Math.sin((54 + i * 72) / 180 * Math.PI) * 80 + 200);
    // }
    // context.closePath();
    // //设置边框样式以及填充颜色
    // context.lineWidth = "3";
    // context.fillStyle = "#F6F152";
    // context.strokeStyle = "#F5270B";
    // context.fill();
    // context.stroke();
  }
}
