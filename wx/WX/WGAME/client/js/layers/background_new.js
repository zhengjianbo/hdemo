import Sprite from '../base/sprite'

var util = require('../../js/utils/util.js')
var geometry = require('../../js/utils/geometry.js')

const screenWidth = window.innerWidth
const screenHeight = window.innerHeight

const BG_IMG_SRC = 'images/bg.jpg'
const BG_WIDTH = 512
const BG_HEIGHT = 512


export default class BackGround extends Sprite {
  
  constructor(ctx,json) {
    super(BG_IMG_SRC, BG_WIDTH, BG_HEIGHT)
   
    this.top = 0
    this.setupJson=json;
      this.renderFirst(ctx)
  }

  //定时更新
  update() {
    // this.top += 2 
    // if ( this.top >= screenHeight )
    //   this.top = 0
  }
  render(context) {
    //根据配置来获取
    context.beginPath();
    context.fillStyle = "#FFFFFF";
    context.fillRect(0, 0, screenWidth, screenHeight)
    
    geometry.drawRect(context, {
      strokeColor: "#00FF00",
      x: 50,
      y: 150,
      angle: -10,
      width: 200,
      heigth: 260,
      lineWidth: 1,
      fillColor: "#FF0000"
    });
    // geometry.drawCircle(context, {
    //   strokeColor: "#00FF00",
    //   x: 50,
    //   y: 150,
    //   r: 10,
    //   start: 0,
    //   end: 360,
    //   lineWidth: 1,
    //   fillColor: "#FF0000"
    // });
    // geometry.drawLine(context, {
    //   strokeColor: "#00FF00",
    //   p1: {
    //     x: 50,
    //     y: 150,
    //   },
    //   p2: {
    //     x: 180,
    //     y: 250,
    //   },
    //   lineWidth: 1
    // });
    // geometry.drawTriangle(context, {
    //   p1: {
    //     x: 100,
    //     y: 150,
    //   },
    //   p2: {
    //     x: 200,
    //     y: 150,
    //   },
    //   p3: {
    //     x: 150,
    //     y: 200,
    //   },
    //   fillColor: '#FF0000',
    //   strokeColor: '#FF0000'
    // })
  }

  renderFirst(context) {
    this.render(context);
  }
}