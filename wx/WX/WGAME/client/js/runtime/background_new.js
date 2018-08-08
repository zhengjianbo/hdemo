import Sprite from '../base/sprite'

const screenWidth  = window.innerWidth
const screenHeight = window.innerHeight

 const BG_IMG_SRC   = 'images/bg.jpg'
const BG_WIDTH     = 512
const BG_HEIGHT    = 512

 
export default class BackGround extends Sprite {
  constructor(ctx) {
    super(BG_IMG_SRC, BG_WIDTH, BG_HEIGHT)

    this.render(ctx)

    this.top = 0
  }
//定时更新
  update() {
    // this.top += 2 
    // if ( this.top >= screenHeight )
    //   this.top = 0
  }
 
  render(context) {
    context.moveTo(10, 10);
    context.lineTo(200, 200);
    //设置样式
    context.lineWidth = 2;
    context.strokeStyle = "#F5270B";
    //绘制
    context.stroke();
  }
}
