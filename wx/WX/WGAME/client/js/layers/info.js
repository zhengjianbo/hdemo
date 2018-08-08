import Sprite from '../base/sprite'

const screenWidth  = window.innerWidth
const screenHeight = window.innerHeight

 const BG_IMG_SRC   = 'images/bg.jpg'
const BG_WIDTH     = 512
const BG_HEIGHT    = 512

 
export default class GameInfo extends Sprite {
  constructor(ctx) {
    super(BG_IMG_SRC, BG_WIDTH, BG_HEIGHT)
 
    // let button = wx.createFeedbackButton({
    //   type: 'image',
    //   image:'',
    //   text: '打开意见反馈页面',
    //   style: {
    //     left: 10,
    //     top: 76,
    //     width: 100,
    //     height: 40,
    //     lineHeight: 40,
    //     backgroundColor: '#ff0000',
    //     color: '#ffffff',
    //     textAlign: 'center',
    //     fontSize: 16,
    //     borderRadius: 4
    //   }
    // })

    // let button = wx.createUserInfoButton({
    //   type: 'text',
    //   text: '获取用户信息',
    //   style: {
    //     left: 10,
    //     top: 76,
    //     width: 200,
    //     height: 40,
    //     lineHeight: 40,
    //     backgroundColor: '#ff0000',
    //     color: '#ffffff',
    //     textAlign: 'center',
    //     fontSize: 16,
    //     borderRadius: 4
    //   }
    // })
    // button.onTap((res) => {
    //   console.log(res.errMsg)
    //   console.log(res)
    // })
   

    // let button = wx.createOpenSettingButton({
    //   type: 'text',
    //   text: '打开设置页面',
    //   style: {
    //     left: 10,
    //     top: 76,
    //     width: 200,
    //     height: 40,
    //     lineHeight: 40,
    //     backgroundColor: '#ff0000',
    //     color: '#ffffff',
    //     textAlign: 'center',
    //     fontSize: 16,
    //     borderRadius: 4
    //   }
    // })

    let button = wx.createGameClubButton({
      icon: 'green',
      style: {
        left: 10,
        top: 76,
        width: 40,
        height: 40
      }
    })

    // var rect= wx.getMenuButtonBoundingClientRect(); 
    // console.log("===" + rect.width+","+rect.height);
   // this.render(ctx)
 
  } 
  update() {
    
  }
 
  render(context) {
      
  }
}
