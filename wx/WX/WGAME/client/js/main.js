import Player     from './player/index'
import Enemy      from './npc/enemy'
import BackGround from './layers/background_new'
import Game from './layers/game'
import GameInfo from './layers/info'
//import GameInfo   from './runtime/gameinfo'
import Music      from './runtime/music'
import DataBus    from './databus'

let ctx   = canvas.getContext('2d')
let databus = new DataBus()

/**
 * 游戏主函数
 */
export default class Main {
  constructor() {
    // 维护当前requestAnimationFrame的id
    this.aniId    = 0
      
    this.restart()
  }

  restart() {
     
    canvas.removeEventListener(
      'touchstart',
      this.touchHandler
    )
    
    this.gameinfo = new GameInfo(ctx)
    this.bg       = new BackGround(ctx)
    
    this.bindLoop     = this.loop.bind(this)
    this.hasEventBind = false

    // 清除上一局的动画
    window.cancelAnimationFrame(this.aniId);

    this.aniId = window.requestAnimationFrame(
      this.bindLoop,
      canvas
    )
 
  }
  
  //游戏结束后的触摸事件处理逻辑
  touchEventHandler(e) {
     e.preventDefault()

    let x = e.touches[0].clientX
    let y = e.touches[0].clientY

    let area = this.gameinfo.btnArea

    if (   x >= area.startX
        && x <= area.endX
        && y >= area.startY
        && y <= area.endY  )
      this.restart()
    }
 
 render() {
      ctx.clearRect(0, 0, canvas.width, canvas.height) 
      this.bg.render(ctx)
 
  }

  // 游戏逻辑更新主函数
  update() {
   
    this.bg.update()
 
  }

  // 实现游戏帧循环
  loop() {
   
    this.update()
    this.render()

    this.aniId = window.requestAnimationFrame(
      this.bindLoop,
      canvas
    )
  }
}
