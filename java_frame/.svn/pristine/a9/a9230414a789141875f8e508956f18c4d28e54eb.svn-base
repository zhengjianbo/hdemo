//表单设计 https://www.pocketmagic.net/wp-content/uploads/2012/04/AndroidKeyInjector.zip
var removeHtml='<div class="lr-compont-remove"><i title="移除组件" class="del fa fa-close"></i></div>';

var layui_form = layui.form;
var laydate = layui.laydate;
var laypage = layui.laypage ;
var layer = layui.layer;
var laytable = layui.table;

var layHtml='<div class="layui-form-item layui-form-pane">     <label class="layui-form-label" style="width:80px">占比</label>     <div class="layui-input-block" style="margin-left:80px">     <select id="control_layout" name="control_layout" lay-filter="layout"> '+
		 '<option value=  "layui-col-xs1">1</option>'
		+'<option value=  "layui-col-xs2">2</option>'
		+'<option value=  "layui-col-xs3">3</option>'
		+'<option value=  "layui-col-xs4">4</option>' 
		+'<option value=  "layui-col-xs5">5</option>' 
		+'<option value=  "layui-col-xs6">6</option>' 
		+'<option value=  "layui-col-xs7">7</option>'
		+'<option value=  "layui-col-xs8">8</option>'
		+'<option value=  "layui-col-xs9">9</option>'
		+'<option value=  "layui-col-xs10">10</option>' 
		+'<option value=  "layui-col-xs11">11</option>' 
		+'<option value=  "layui-col-xs12">12</option>' 
		+'</select> ' 
		+'  </div>   </div>'; 
//大部分最后显示控件
var layEndHtml=   '  <div class="layui-form-item layui-form-pane  layui-form-text"> ';
layEndHtml += '    <label class="layui-form-label" style="width:100%">生成代码</label> ';
layEndHtml += '     <div class="layui-input-block" style="left:0px;margin-left:0px">  ';
layEndHtml += '       <textarea id="control_html" placeholder="生成内容" class="layui-textarea" style="margin-top: 0px; margin-bottom: 0px; height: 100px;"></textarea></div> ';
layEndHtml += '    </div> ';
layEndHtml +='  <div class="layui-form-item layui-form-pane  layui-form-text"> ';
layEndHtml += '  <button type="button"  id="bt_code" class="layui-btn layui-btn-sm layui-btn-normal">代码生成</button>    ';
layEndHtml += '       ';
layEndHtml += '    </div> ';

	//编码 
 function html_decode (str) 
 { 
  var s = ""; 
 if (str==undefined||str.length == 0) return ""; 
  s = str.replace(/&/g, ">"); 
  s = s.replace(/</g, "<"); 
  s = s.replace(/>/g, ">"); 
  s = s.replace(/ /g, " "); 
  s = s.replace(/\'/g, "'"); 
  s = s.replace(/\"/g, "\""); 
  s = s.replace(/\n/g, "<br>"); 
  console.log("s:"+s);
 return s; 
 } 
 //解码 
 function html_encode(str) 
 { 
 var s = ""; 
 if (str==undefined||str.length == 0) return ""; 
  s = str.replace(/>/g, "&gt;"); 
  s = s.replace(/</g, "&lt;");  
  s = s.replace(/ /g, " "); 
  s = s.replace(/'/g, "\'"); 
  s = s.replace(/"/g, "\""); 
  s = s.replace(/<br>/g, "\n");
 return s; 
 }
 
 
$.fn.frmDesign = function (options) {
    var $frmdesigh = $(this);
    if (!$frmdesigh.attr('id')) {
        return false;
    }
    var defaults = {
        controlDataJson: [],
        tablefiledJsonData: "",
        frmContent: "",
        Height: 572,
        url: "",
        param: "",
        isSystemTable: 0,//默认是不绑定数据表的
        getData: function (isTest) {
            var postData = [];
            var j = 0;
            var _controlfieldHistory = {};
			
			var form_items=$("#app_layout_list").find('*[data-value]');
			if(form_items==undefined||form_items==null||form_items.length==0){
				
			}else{
				//按顺序读取
				$.each(form_items,function(index,form_item){
					 var rowJson = options.controlDataJson[$(form_item).attr('data-value')];
					 postData.push(rowJson);
				});
				return postData;
			} 
            for (var i in options.controlDataJson) {
                var rowJson = options.controlDataJson[i];
                if (rowJson.control_field == "" && isTest != true) {
                    dialogTop("请输入字段Id", "error");
                    $("#app_layout_list").find('[data-value=' + i + ']').addClass('activeerror');
                    return false;
                }
                if (rowJson.control_label == "" && isTest != true) {
                    dialogTop("请输入字段名称", "error");
                    $("#app_layout_list").find('[data-value=' + i + ']').addClass('activeerror');
                    return false;
                }
                if (isTest != true)
                {
                    if (_controlfieldHistory[rowJson.control_field] != undefined)
                    {
                        dialogTop("字段Id有重复", "error");
                        return false;
                    }
                    _controlfieldHistory[rowJson.control_field] = "1";
                }

                if (rowJson.control_item != undefined) {
                    var controlitem = [];
                    for (var j in rowJson.control_item) {
                        controlitem.push(rowJson.control_item[j]);
                    }
                    rowJson.control_item = controlitem;
                }
                j++;
                postData.push(rowJson);
            }
            if (j == 0 && isTest != true)
            {
                dialogTop("不能是空表单", "error");
                return false;
            }
            return postData;
        }
    };
	
    var options = $.extend(defaults, options);

    var divhtml = '<div class="app_body">'; 
	divhtml += '    <div id="app_layout_option" class="field_option notclose">';
    divhtml += '    </div>'; 
	divhtml += '<div id="move_item_list" class="app_field">';
    divhtml += '<div class="item_row"><i id="include" class="fa fa-icon"></i>Include</div>';
    divhtml += '<div class="item_row"><i id="div" class="fa fa-icon"></i>&lt;div&gt;</div>';
    divhtml += '<div class="item_row"><i id="div_end" class="fa fa-icon"></i>&lt;/div&gt;</div>';
    divhtml += '<div class="item_row"><i id="form" class="fa fa-icon"></i>&lt;form&gt;</div>';
    divhtml += '<div class="item_row"><i id="form_end" class="fa fa-icon"></i>&lt;/form&gt;</div>';
    divhtml += '<div class="item_row"><i id="label" class="fa fa-icon"></i>标签</div>';
    divhtml += '<div class="item_row"><i id="button" class="fa fa-button"></i>按钮</div>';
    divhtml += '<div class="item_row"><i id="text" class="fa fa-italic"></i>文本框</div>';
    divhtml += '<div class="item_row"><i id="textarea" class="fa fa-align-justify"></i>文本区</div>';
    divhtml += '<div class="item_row"><i id="radio" class="fa fa-circle-thin"></i>单选框</div>';
    divhtml += '<div class="item_row"><i id="checkbox" class="fa fa-square-o"></i>多选框</div>';
    divhtml += '<div class="item_row"><i id="select" class="fa fa-caret-square-o-right"></i>下拉框</div>';
    divhtml += '<div class="item_row"><i id="datetime" class="fa fa-calendar"></i>日期框</div>';  
    divhtml += '<div class="item_row"><i id="html" class="fa fa-icon"></i>HTML</div>'; 
    divhtml += '<div class="item_row"><i id="texteditor" class="fa fa-edit"></i>编辑器</div>'; 
    divhtml += '<div class="item_row"><i id="image" class="fa fa-photo"></i>图片</div>';
    divhtml += '<div class="item_row"><i id="upload" class="fa fa-paperclip"></i>附件</div>';
    divhtml += '<div class="item_row"><i id="pager" class="fa fa-icon"></i>分页表格</div>';
  //  divhtml += '<div class="item_row"><i id="table" class="fa fa-icon"></i></div>';
    divhtml += '</div>'; 
    divhtml += '<div class="app_layout" >';
    divhtml += '    <div id="app_layout_list" class="item_table notclose connectedSortable">';
    divhtml += '        <div class="guideareas"></div>';
    divhtml += '    </div>'; 
    divhtml += '</div>';
   
    divhtml += '</div>';
    $frmdesigh.html(divhtml);
     
    $frmdesigh.find(".app_body").height(options.Height);
    $frmdesigh.find(".field_option").height(options.Height - 14).css("right", -240);
    $frmdesigh.find(".guideareas").height(options.Height - 33);
    var item_field_value_width = $(window).width() - 558;
    //表单控件拖动
    function formdesigner_move() {
        $("#move_item_list .item_row").draggable({
            connectToSortable: "#app_layout_list",
            helper: "clone",
            revert: "invalid"
        });
        $("#app_layout_list").sortable({
            opacity: 0.4,
            delay: 300,
            cursor: 'move',
            placeholder: "ui-state-highlight",
            stop: function (event, ui) {
                var random_id = String(Math.random()).substr(2);
                var $item_control = null;
                var $this_place = $(ui.item[0]);
                var controltype = $this_place.find('i').attr('id');
				
				var title = $this_place.text();
				
                var controlrowJson = {};
                controlrowJson["control_type"] = controltype;
				 
                if (!!controltype) {  
						$item_control = $('<div class="lr-compont-title"><span>'+html_encode(title)+'</span>'+removeHtml+'</div><div class="lr-compont-value">'+title+'</div> ');
                            controlrowJson["control_label"] =title; 
                            controlrowJson["control_field"] =title; 
                            options.controlDataJson[random_id] = controlrowJson; 
                    if ($item_control) { 
						 $this_place.attr('class',"lr-compont-item ui-draggable layui-col-xs12");
						//  $this_place.attr('style',""); 
                        $this_place.html($item_control); 
                        $this_place.attr('data-value', random_id);
                        item_rowclick();
                        $this_place.trigger("click");
                    }
                } else {
                    $this_place.trigger("click");
                }
            },
            start: function (event, ui) {
                $(".guideareas").hide()
                $(".ui-state-highlight").html('拖放控件到这里');
                $(".field_option").hide();
                $("#app_layout_list .lr-compont-item ").removeClass('active')
            },
            out: function (event, ui) {
                if (ui.helper != null) {
                    var falg = true;
                    for (item in options.controlDataJson) {
                        falg = false;
                        break;
                    }
                    if (falg) {
                        $(".guideareas").show();
                    }
                }
            }
        });
        item_rowclick();
        function item_rowclick() { 
              $("#app_layout_list .lr-compont-item ").unbind('click');
              $("#app_layout_list .lr-compont-item ").click(function () { 
                  var $this = $(this);
                  var $field_option = $(".field_option");
                  $("#app_layout_list .lr-compont-item ").removeClass('active').removeClass('activeerror');
                  $this.addClass('active');
                  $('.field_option').animate({ right: 0, speed: 2000 }).show();
                  initControlProperty($this); 
              });
			 loadRemove();
        }
		function loadRemove(){
			$("#app_layout_list .lr-compont-remove").unbind('click');
            $('#app_layout_list .lr-compont-remove').click(function () {
				 
                var $item_row = $(this).parents('.lr-compont-item');
                 delete options.controlDataJson[$item_row.attr('data-value')];
                $item_row.remove();
                $(".field_option").animate({ right: -240, speed: 2000 });
                var falg = true;
                for (item in options.controlDataJson) {
                    falg = false;
                    break;
                }
                if (falg) {
                    $(".guideareas").show();
                }
            });
			
		}
        //初始化控件属性
        function initControlProperty(e) {
            var rowJson = options.controlDataJson[e.attr('data-value')]; 
            if (rowJson) {
				//每个元素 不同的显示右边编辑界面
                render_form_unit(e,rowJson);
            } 
        }
        //获取表单
        if (options.frmContent != "" && options.frmContent != null)
        {
            frmToHtml(options.frmContent);
        }
        else if (options.url != "") {
		  //从ajax获取
          // $.ajax({
          //     url: options.url,
          //     data: options.param,
          //     type: "GET",
          //     dataType: "json",
          //     async: false,
          //     success: function (data) {
          //         if (data.FrmContent != null)
          //         {
          //             frmToHtml(data.FrmContent);
          //         }
          //     },
          //     error: function (XMLHttpRequest, textStatus, errorThrown) {
          //         dialogMsg(errorThrown, -1);
          //     }
          // });
        }
        //json=>html
        function frmToHtml(frmContent) {
            var frmContentJson = jQuery.parseJSON(frmContent);//eval('(' + frmContent + ')');
            var num = 0;
            $app_layout_list = $('#app_layout_list');
            $.each(frmContentJson, function (id, item) {
                $(".guideareas").hide();
                var random_id = String(Math.random()).substr(2);
                options.controlDataJson[random_id] = item;
                var controlitemstr = "";
				//显示checkbox radios 选项
                if (item.control_item != undefined) {
                    var controlitem = [];
                    $.each(item.control_item, function (i, n) {
                        controlitem[n.identify] = n;
                        if (controlitemstr != "") {
                            controlitemstr += "、";
                        }
                        controlitemstr += n.name;
                    });
                    options.controlDataJson[random_id].control_item = controlitem;
                }
				
                var controlrequired = item.control_required == '1' ? '<font face="宋体">*</font>' : '';
                var _item_field_value = item.control_fieldname+"";
                 
				 var controlLayout=item.control_layout;
				 if(controlLayout==undefined||controlLayout==null||controlLayout==""){
					 controlLayout="layui-col-xs12";
				 }
				 
                var $item_control = $('<div class="lr-compont-item ui-draggable '+controlLayout+'" ><div class="lr-compont-title"><span>' + html_encode(item.control_label) + '</span>' +removeHtml+ controlrequired + '</div><div class="lr-compont-value">' + _item_field_value + controlitemstr + '</div></div>');
                if ($item_control) {
                    $item_control.attr('data-value', random_id);
                    $app_layout_list.append($item_control);
                    item_rowclick();
                    if (num == 0) {
                        $item_control.trigger("click");
                    }
                    num++;
                }
            });
            $app_layout_list.find('.item_field_value').width(item_field_value_width);
        }
    }
    formdesigner_move();
    

	function form_unit_common(e_row,rowJson){
		var formunit_property_common= '';
		var htext=html_decode(rowJson.control_field); 
        formunit_property_common += '<div class="field_tips"><i class="fa fa-info-circle"></i><span>'+html_encode(htext)+'</span></div>';
		
       // formunit_property_common += '<div class="field_title">ID</div>';
       // formunit_property_common += '<div class="field_control"><input id="control_field_id" type="text" class="form-control"></input></div>'; 
		formunit_property_common +='<div class="layui-form-item layui-form-pane">     <label class="layui-form-label" style="width:80px">id</label>     <div class="layui-input-block" style="margin-left:80px">       <input type="text" id="control_field_id"  name="control_field_id" lay-verify="required" placeholder="请输入" autocomplete="off" class="layui-input">     </div>   </div>';
        // formunit_property_common += '<div class="field_title hide">标识</div>';
      //  formunit_property_common += '<div class="field_control hide"><div id="control_field" type="select" class="ui-select"></div></div>'; 
       formunit_property_common +='<div class="layui-form-item layui-form-pane">     <label class="layui-form-label" style="width:80px">说明</label>     <div class="layui-input-block" style="margin-left:80px">       <input type="text" id="control_label"  name="control_label"  placeholder="请输入" autocomplete="off" class="layui-input">     </div>   </div>';
      
		//formunit_property_common += '<div class="field_title">说明</div>';
       // formunit_property_common += '<div class="field_control"><input id="control_label" type="text" class="form-control" placeholder="必填项"/></div>';
         
		formunit_property_common += layHtml; 
		
	   //formunit_property_common += '<div class="field_title">是否隐藏</div>';
      //  formunit_property_common += '<div class="field_control"><div class="checkbox notclose" style="padding-left: 3px;"><label><input id="control_show" type="checkbox" />隐藏</label></div></div>';
		
		// formunit_property_common += '<div class="field_title">设置</div>';
      //  formunit_property_common += '<div class="field_control"><div class="checkbox notclose" style="padding-left: 3px;"><label><input id="control_required" type="checkbox" />必填</label></div></div>';
		
		return formunit_property_common;
	}
	function form_unit_html(e_row,item){
	  var formunit_property_common='';
		//formunit_property_common += '<div class="field_title">HTML内容</div>';
       // formunit_property_common += '<div class="field_control"> <textarea id="control_html"></textarea></div>'; 
        
//        formunit_property_common += '  <div class="layui-form-item layui-form-pane  layui-form-text"> ';
//        	formunit_property_common += '    <label class="layui-form-label" style="width:100%">HTML内容</label> ';
//        		formunit_property_common += '     <div class="layui-input-block" style="left:0px;margin-left:0px">  ';
//        			formunit_property_common += '       <textarea id="control_html" placeholder="请输入内容" class="layui-textarea" style="margin-top: 0px; margin-bottom: 0px; height: 100px;"></textarea></div> ';
//        				formunit_property_common += '    </div> ';
        
		return formunit_property_common;
	}
	//分页
	function form_unit_pager(e_row,item){
		  var formunit_property_common=''; 
		  formunit_property_common +='<div class="layui-form-item layui-form-pane">     <label class="layui-form-label" style="width:80px">数据</label>     <div class="layui-input-block" style="margin-left:80px">       <input type="text" id="control_default"  name="control_default"  placeholder="显示时从上下文传入beetl变量,或者常量" autocomplete="off" class="layui-input">     </div>   </div>';
	        formunit_property_common +='<div class="layui-form-item layui-form-pane">     <label class="layui-form-label" style="width:80px">当前页数</label>     <div class="layui-input-block" style="margin-left:80px">       <input type="text" id="control_page"  name="control_page"  placeholder="显示时从上下文传入beetl变量,或者常量" autocomplete="off" class="layui-input">     </div>   </div>';
	        formunit_property_common +='<div class="layui-form-item layui-form-pane">     <label class="layui-form-label" style="width:80px">记录总数</label>     <div class="layui-input-block" style="margin-left:80px">       <input type="text" id="control_count"  name="control_count"  placeholder="显示时从上下文传入beetl变量,或者常量" autocomplete="off" class="layui-input">     </div>   </div>';
	        formunit_property_common +='<div class="layui-form-item layui-form-pane">     <label class="layui-form-label" style="width:80px">分页栏</label>     <div class="layui-input-block" style="margin-left:80px">      <input type="checkbox" name="control_pager_show" id="control_pager_show" value="1" title="显示" lay-skin="primary" lay-filter="control_pager_show" >   </div>   </div>';
		       
	       
	        formunit_property_common +='<div class="layui-form-item layui-form-pane " >     <label class="layui-form-label" style="width:100%">分页显示</label>  </div>  '
				  +'<div class="layui-form-item layui-form-pane pane"  style="margin-top:-45px;">  <div class="layui-input-block" style="width:100%;margin-left:0px;">   '
				  	+'<input type="checkbox" name="control_pager_style" id="control_pager_count" value="count" title="记录数" lay-skin="primary" lay-filter="control_pager_style" > '
				  	+'<input type="checkbox" name="control_pager_style" id="control_pager_prev" value="prev" title="上一页" lay-skin="primary" lay-filter="control_pager_style" >  '
				  	+'<input type="checkbox" name="control_pager_style" id="control_pager_page" value="page" title="页数" lay-skin="primary" lay-filter="control_pager_style"  > '

				  	+'<input type="checkbox" name="control_pager_style" id="control_pager_next" value="next" title="下一页" lay-skin="primary" lay-filter="control_pager_style" >  '
				  	+'<input type="checkbox" name="control_pager_style" id="control_pager_limit" value="limit" title="每页记录数" lay-skin="primary" lay-filter="control_pager_style" >  '
				  	+'<input type="checkbox" name="control_pager_style" id="control_pager_skip" value="skip" title="跳转页数" lay-skin="primary" lay-filter="control_pager_style" >  '
				  	+'  </div>    </div>';
			return formunit_property_common;
		}
	//数据表格
	function form_unit_table(e_row,item){
		  var formunit_property_common=''; 
	        formunit_property_common +='<div class="layui-form-item layui-form-pane">     <label class="layui-form-label" style="width:80px">值</label>     <div class="layui-input-block" style="margin-left:80px">       <input type="text" id="control_default"  name="control_default"  placeholder="显示时从上下文传入beetl变量,或者常量" autocomplete="off" class="layui-input">     </div>   </div>';
	         
			return formunit_property_common;
		}
	
	function form_unit_editor(e_row,item){
		  var formunit_property_common=''; 
	        formunit_property_common +='<div class="layui-form-item layui-form-pane">     <label class="layui-form-label" style="width:80px">值</label>     <div class="layui-input-block" style="margin-left:80px">       <input type="text" id="control_default"  name="control_default"  placeholder="显示时从上下文传入beetl变量,或者常量" autocomplete="off" class="layui-input">     </div>   </div>';
	         
			return formunit_property_common;
		}
	function form_unit_upload(e_row,item){
		  var formunit_property_common=''; 
	        formunit_property_common +='<div class="layui-form-item layui-form-pane">     <label class="layui-form-label" style="width:80px">格式限制</label>     <div class="layui-input-block" style="margin-left:80px">       <input type="text" id="control_ext"  name="control_ext"  placeholder="" autocomplete="off" class="layui-input">     </div>   </div>';
	        formunit_property_common +='<div class="layui-form-item layui-form-pane">     <label class="layui-form-label" style="width:80px">上传地址</label>     <div class="layui-input-block" style="margin-left:80px">       <input type="text" id="control_url"  name="control_url"  placeholder="" autocomplete="off" class="layui-input">     </div>   </div>';
			return formunit_property_common;
		}
	
	//label
	function form_unit_label(e_row,item){
	  var formunit_property_common='';
		//formunit_property_common += '<div class="field_title">HTML内容</div>';
      //  formunit_property_common += '<div class="field_control"> <textarea id="control_html"></textarea></div>'; 
		return formunit_property_common;
	}
	//radios
	function form_unit_radios(e_row,item){
	  var formunit_property_common='';
	   
		 formunit_property_common +='<div class="layui-form-item layui-form-pane " >     <label class="layui-form-label" style="width:80px">枚举</label>     <div class="layui-input-block" style="margin-left:80px">       <input type="text" id="control_enum"  name="control_enum"  lay-verify="required" placeholder="请输入" autocomplete="off" class="layui-input">     </div>   </div>';
		 formunit_property_common +='<div class="layui-form-item layui-form-pane " >     <label class="layui-form-label" style="width:80px">KEY</label>     <div class="layui-input-block" style="margin-left:80px">       <input type="text" id="control_key"  name="control_key" lay-verify="required" placeholder="请输入" autocomplete="off" class="layui-input">     </div>   </div>';
		 formunit_property_common +='<div class="layui-form-item layui-form-pane " >     <label class="layui-form-label" style="width:80px">SHOW</label>     <div class="layui-input-block" style="margin-left:80px">       <input type="text" id="control_value"  name="control_value" lay-verify="required"  placeholder="请输入" autocomplete="off" class="layui-input">     </div>   </div>';
			
		
		return formunit_property_common;
	}
	//datetime
	function form_unit_datetime(e_row,item){
	  var formunit_property_common='';
//		formunit_property_common += '<div class="field_title">日期格式</div>'; 
//		formunit_property_common += '<div class="field_control"><input id="control_dateformat" type="text" class="form-control" placeholder="日期格式"/></div>'; 
//		formunit_property_common += '<div class="field_title">值</div>'; 
//		formunit_property_common += '<div class="field_control"><input id="control_default" type="text" class="form-control" placeholder="显示时从上下文传入beetl变量,或者常量"/></div>';   
	  	
	  formunit_property_common +='<div class="layui-form-item layui-form-pane " >     <label class="layui-form-label" style="width:80px">日期范围</label>     <div class="layui-input-block layui-form-label_x" style="margin-left:80px">        <input type="checkbox" name="control_daterange" lay-skin="primary" title="范围" checked="false" class="layui-input">     </div>   </div>';
		
		 formunit_property_common +='<div class="layui-form-item layui-form-pane " >     <label class="layui-form-label" style="width:80px">日期格式</label>     <div class="layui-input-block" style="margin-left:80px">       <input type="text" id="control_dateformat"  name="control_dateformat"  lay-verify="required" placeholder="请输入" autocomplete="off" class="layui-input">     </div>   </div>';
		 formunit_property_common +='<div class="layui-form-item layui-form-pane " >     <label class="layui-form-label" style="width:80px">开始值</label>     <div class="layui-input-block" style="margin-left:80px">       <input type="text" id="control_default_start"  name="control_default_start"    placeholder="请输入" autocomplete="off" class="layui-input">     </div>   </div>';
		 formunit_property_common +='<div class="layui-form-item layui-form-pane " >     <label class="layui-form-label" style="width:80px">结束值</label>     <div class="layui-input-block" style="margin-left:80px">       <input type="text" id="control_default_end"  name="control_default_end"    placeholder="请输入" autocomplete="off" class="layui-input">     </div>   </div>';
				
		
		return formunit_property_common;
	}
	//button
	function form_unit_button(e_row,item){
		  var formunit_property_common='';
			//按钮  
		  formunit_property_common +='<div class="layui-form-item layui-form-pane " >     <label class="layui-form-label" style="width:80px">按钮样式</label>     <div class="layui-input-block" style="margin-left:80px">       <input type="text" id="control_style"  name="control_style"  placeholder="请输入" autocomplete="off" class="layui-input">     </div>   </div>';
		     
		//   formunit_property_common += '<div class="field_title">按钮样式<i title="按钮样式" class="help fa fa-question-circle"></i></div>';
	    //    formunit_property_common += '<div class="field_control"><input id="control_style" type="text" class="form-control" placeholder="无则不填"/></div>';
	        
		  formunit_property_common +='<div class="layui-form-item layui-form-pane " >     <label class="layui-form-label" style="width:100%">按钮类型</label>  </div>  '
			  +'<div class="layui-form-item layui-form-pane pane"  style="margin-top:-45px;">  <div class="layui-input-block" style="width:100%;margin-left:0px;">   '
			  	+'  <input type="radio" name="control_buttontype" value="button" title="普通"  lay-filter="control_buttontype" > '
			  	+'<input type="radio" name="control_buttontype" value="rest" title="Rest" lay-filter="control_buttontype" >  '
			  	+'<input type="radio" name="control_buttontype" value="submit" title="提交" lay-filter="control_buttontype"  >   </div>   </div>';
			  
	        formunit_property_common +='<div class="layui-form-item layui-form-pane " >     <label class="layui-form-label" style="width:80px">点击事件</label>     <div class="layui-input-block" style="margin-left:80px">       <input type="text" id="control_click"  name="control_click"  placeholder="请输入" autocomplete="off" class="layui-input">     </div>   </div>';
	    	
	        
			return formunit_property_common;
	}
	
	//include
	function form_unit_include(e_row,item){
		  var formunit_property_common=''; 
		  
		  formunit_property_common +='<div class="layui-form-item layui-form-pane">     <label class="layui-form-label" style="width:80px">载入区域</label>     <div class="layui-input-block" style="margin-left:80px">       <select id="control_include" name="control_include" lay-filter="include">  <option value="">==请选择==</option><option value="Num">数字</option>  </select>    </div>   </div>';
	        
		  formunit_property_common +='<div class="layui-form-item layui-form-pane " >     <label class="layui-form-label" style="width:80px">异步加载</label>     <div class="layui-input-block layui-form-label_x" style="margin-left:80px">        <input type="checkbox" name="control_isajax" name="control_isajax" lay-skin="primary" title="异步"   lay-filter="ajax" class="layui-input" value="1">     </div>   </div>';
			 
		 return formunit_property_common;
	}

//form start
	function form_unit_form(e_row,item){
	  var formunit_property_common=''; 
	//  formunit_property_common += '<div class="field_title">表单内容<i title="表单内容" class="help fa fa-question-circle"></i></div>';
    //  formunit_property_common += '<div class="field_control"><input id="control_style" type="text" class="form-control" placeholder="无则不填"/></div>';
          
		return formunit_property_common;
	}	//div start
	function form_unit_div(e_row,item){
	  var formunit_property_common=''; 
	 // 、、 formunit_property_common += '<div class="field_title">div内容<i title="div内容" class="help fa fa-question-circle"></i></div>';
     // 、、  formunit_property_common += '<div class="field_control"><input id="control_style" type="text" class="form-control" placeholder="无则不填"/></div>';
          
		return formunit_property_common;
	}
	//checkbox
	function form_unit_checkbox(e_row,item){ 
		return form_unit_radios(e_row,item);
	}
	//文本输入框
	function form_unit_textinput(e_row,item){
		var formunit_property_common=''; 
        formunit_property_common +='<div class="layui-form-item layui-form-pane">     <label class="layui-form-label" style="width:80px">字段验证</label>     <div class="layui-input-block" style="margin-left:80px">       <select id="control_verify" name="control_verify" lay-filter="verify">  <option value="">==请选择==</option><option value="Num">数字</option><option value="Double">小数</option><option value="Phone">必须电话格式</option><option value="Mobile">手机格式</option><option value="Email">电子邮件格式</option><option value="IDCard">身份证格式</option> </select>    </div>   </div>';
        formunit_property_common +='<div class="layui-form-item layui-form-pane">     <label class="layui-form-label" style="width:80px">值</label>     <div class="layui-input-block" style="margin-left:80px">       <input type="text" id="control_default"  name="control_default"  placeholder="显示时从上下文传入beetl变量,或者常量" autocomplete="off" class="layui-input">     </div>   </div>';
         
		return formunit_property_common;
	}
	function form_unit_end(e_row,item){
		var formunit_property_common=layEndHtml; 
		return formunit_property_common;
	}
	//render
	function render_form_unit(e_row,item) { 
		var _html = '';
        _html +=  form_unit_common(e_row,item); 
		 switch (item.control_type) {
			case "text":
                _html +=  form_unit_textinput(e_row,item); 
                break;
			case "label":
                _html +=  form_unit_label(e_row,item); 
                break;
			case "radio":
                _html +=  form_unit_radios(e_row,item); 
                break;
			case "select":
                _html +=  form_unit_radios(e_row,item); 
                break; 
			case "checkbox":
                _html +=  form_unit_checkbox(e_row,item); 
                break;
			case "datetime":
                _html +=  form_unit_datetime(e_row,item); 
                break;
			case "button":
                _html +=  form_unit_button(e_row,item); 
                break;
			case "div":
                _html +=  form_unit_div(e_row,item); 
                break;
			case "form":
                _html +=  form_unit_form(e_row,item);
				 break; 
			case "include":
                _html +=  form_unit_include(e_row,item);  
                break;	
			case "html":
                _html +=  form_unit_html(e_row,item); 
                break;
			case "texteditor":
                _html +=  form_unit_editor(e_row,item); 
                break;	
			case "upload":
                _html +=  form_unit_upload(e_row,item);  
                break;
			case "pager":
                _html +=  form_unit_pager(e_row,item);  
                break;
			case "table":
                _html +=  form_unit_table(e_row,item);  
                break;	
		 }  
         _html +=  form_unit_end(e_row,item); 
         
        var $html = $(_html);
        $(".field_option").html($html);
 
        var rowJson = options.controlDataJson[e_row.attr('data-value')];
        if (rowJson == null) {
        	layui_form.render();
            return false;
        }
		form_unit_Property(e_row,rowJson,$html);

        layui_form.render();
	}
	function form_unit_Property(e_row,rowJson,$html) {
        
       // setControlField(rowJson, e_row, $html);
        $html.find('#control_field_id').val(rowJson.control_field_id);
        $html.find('#control_label').val(rowJson.control_label);
        $html.find('#control_default').val(rowJson.control_default);
		if(rowJson.control_layout==null||rowJson.control_layout==""){
			rowJson.control_layout="layui-col-xs12"; 
		} 
        $html.find('#control_layout').val(rowJson.control_layout);
        
        $html.find('#control_style').val(rowJson.control_style); 
        $html.find('#control_verify').val(rowJson.control_verify); 
        $html.find('#control_enum').val(rowJson.control_enum); 
        $html.find('#control_key').val(rowJson.control_key); 
        $html.find('#control_value').val(rowJson.control_value); 
      

        $html.find('#control_default_start').val(rowJson.control_default_start);
        $html.find('#control_default_end').val(rowJson.control_default_end);
        
        $html.find('#control_dateformat').val(rowJson.control_dateformat);
        
		    laydate.render({ 
		  		  elem: '#control_default_start'
		  		 ,format: 'yyyy-MM-dd'
		  	});
		
		  	laydate.render({ 
		  		  elem: '#control_default_end'
		  		 ,format: 'yyyy-MM-dd'
		  	}); 
  	
		$html.find('#control_html').val(rowJson.control_html);
		$html.find('#control_include_paras').val(rowJson.control_include_paras);
		$html.find('#control_click').val(rowJson.control_click); 

		$('[name=control_pager_show]').each(function(i,item){  
		    if($(item).val()==rowJson.control_pager_show){  
		        $(item).prop('checked',true);  
		    }
		});  
		layui_form.on('checkbox(control_pager_show)', function(data){ 
        	rowJson["control_pager_show"] = data.elem.checked;
            options.controlDataJson[e_row.attr('data-value')] = rowJson;  
		}); 
		$('[name=control_buttontype]').each(function(i,item){  
		    if($(item).val()==rowJson.control_buttontype){  
		        $(item).prop('checked',true);  
		    }
		});   
 
		$('[name=control_pager_style]').each(function(i,item){  
		    if($(item).val()==rowJson.control_pager_count){  
		        $(item).prop('checked',true);  
		    }else  if($(item).val()==rowJson.control_pager_limit){  
		        $(item).prop('checked',true);  
		    }else  if($(item).val()==rowJson.control_pager_prev){  
		        $(item).prop('checked',true);  
		    }else  if($(item).val()==rowJson.control_pager_page){  
		        $(item).prop('checked',true);  
		    }else  if($(item).val()==rowJson.control_pager_next){  
		        $(item).prop('checked',true);  
		    }else  if($(item).val()==rowJson.control_pager_skip){  
		        $(item).prop('checked',true);  
		    }
		});
		layui_form.on('checkbox(control_pager_style)', function(data){
			if( data.elem.checked){ 
				rowJson[$(this).attr('id')] =data.value;
			}else{ 
				rowJson[$(this).attr('id')] =null;
			}
			//rowJson["control_pager_style"] = data.elem.checked;
            options.controlDataJson[e_row.attr('data-value')] = rowJson;    
		}); 
		 
		$html.find('#control_html').keyup(function (e) {
             var value = $(this).val();
            rowJson[$(this).attr('id')] = value;
            options.controlDataJson[e_row.attr('data-value')] = rowJson;  
        });
		$html.find('#control_click').keyup(function (e) {
             var value = $(this).val();
            rowJson[$(this).attr('id')] = value;
            options.controlDataJson[e_row.attr('data-value')] = rowJson;  
        });
		
		$html.find('#control_dateformat').keyup(function (e) {
             var value = $(this).val();
            rowJson[$(this).attr('id')] = value;
            options.controlDataJson[e_row.attr('data-value')] = rowJson; 
            	laydate.render({ 
            		  elem: '#control_default_start'
            		 ,format: value
            	}); 
            	laydate.render({ 
            		  elem: '#control_default_end'
            		 ,format: value
            	});   
        });

		$html.find('#control_default_start').keyup(function (e) {
            var value = $(this).val();
           rowJson[$(this).attr('id')] = value;
           options.controlDataJson[e_row.attr('data-value')] = rowJson;  
       });
		$html.find('#control_default_end').keyup(function (e) {
            var value = $(this).val();
           rowJson[$(this).attr('id')] = value;
           options.controlDataJson[e_row.attr('data-value')] = rowJson;  
       });
		$html.find('#control_enum').keyup(function (e) {
            var value = $(this).val();
           rowJson[$(this).attr('id')] = value;
           options.controlDataJson[e_row.attr('data-value')] = rowJson;  
       });
		
		
		$html.find('#control_enum').keyup(function (e) {
             var value = $(this).val();
            rowJson[$(this).attr('id')] = value;
            options.controlDataJson[e_row.attr('data-value')] = rowJson;  
        });
		$html.find('#control_key').keyup(function (e) {
             var value = $(this).val();
            rowJson[$(this).attr('id')] = value;
            options.controlDataJson[e_row.attr('data-value')] = rowJson;  
        });
		$html.find('#control_value').keyup(function (e) {
             var value = $(this).val();
            rowJson[$(this).attr('id')] = value;
            options.controlDataJson[e_row.attr('data-value')] = rowJson;  
        });
		$html.find('#control_style').keyup(function (e) {
             var value = $(this).val();
            rowJson[$(this).attr('id')] = value;
            options.controlDataJson[e_row.attr('data-value')] = rowJson;  
        });
		$html.find('#control_include_paras').keyup(function (e) {
             var value = $(this).val();
            rowJson[$(this).attr('id')] = value;
            options.controlDataJson[e_row.attr('data-value')] = rowJson;  
        });
		
		$html.find('#control_page').keyup(function (e) {
            var value = $(this).val();
           rowJson[$(this).attr('id')] = value;
           options.controlDataJson[e_row.attr('data-value')] = rowJson;  
       });
		$html.find('#control_count').keyup(function (e) {
            var value = $(this).val();
           rowJson[$(this).attr('id')] = value;
           options.controlDataJson[e_row.attr('data-value')] = rowJson;  
       });
		
		//bt_code
		$("#bt_code").unbind('click');
		$('#bt_code').click(function () {
			 //读取当前事件 control_html
			 var inputHtml="";
			 switch (rowJson.control_type) {
					case "text":
						//如果是正常的文本
						inputHtml+='  <div class=" '+rowJson.control_layout+'_ext "  >    <input type="text" id="'+rowJson.control_field_id+'"  name="'+rowJson.control_field_id+'"  placeholder="'+rowJson.control_label+'" autocomplete="off" class="layui-input" >  </div>  ';		
						break;
					case "label":  
						inputHtml+='  <label class=" '+rowJson.control_layout+'_ext  layui-form-label-ext "  >'+rowJson.control_label+'</label>  ';		
						break;
					case "radio":  
						inputHtml+='  <div class=" '+rowJson.control_layout+'_ext ">  ';
						inputHtml+='<%   ' ;
						inputHtml+='    for(var item in  '+rowJson.control_enum+' ){ ';
						inputHtml+='%>   ';
						inputHtml+='         <input type="radio" name="'+rowJson.control_field_id+'" value="${item.'+rowJson.control_key+'}" title="${item.'+rowJson.control_value+'}" >   ';
						inputHtml+='<%   ' ;
						inputHtml+='    }		';
						inputHtml+='%>   ';
						inputHtml+=' </div>  ';
						break;
					case "select": 
						// <select id="control_layout" name="control_layout" lay-filter="layout">   <option value=  "layui-col-xs1">1</option>'
						inputHtml+='  <div class=" '+rowJson.control_layout+'_ext ">  ';
						inputHtml+='  <select id="'+rowJson.control_field_id+'" name="'+rowJson.control_field_id+'" lay-filter="'+rowJson.control_field_id+'">   ';
						inputHtml+='<%   ' ;
						inputHtml+='    for(var item in  '+rowJson.control_enum+' ){ ';
						inputHtml+='%>   ';
						inputHtml+='           <option value=  "${item.'+rowJson.control_key+'}">${item.'+rowJson.control_value+'}</option>  ';
						inputHtml+='<%   ' ;
						inputHtml+='    }		';
						inputHtml+='%>   ';
						inputHtml+=' </select>  ';
						inputHtml+=' </div>  ';
						break; 
					case "checkbox": 
						inputHtml+='  <div class=" '+rowJson.control_layout+'_ext ">  ';
						inputHtml+='<%   ' ;
						inputHtml+='    for(var item in  '+rowJson.control_enum+' ){ ';
						inputHtml+='%>   ';
						inputHtml+='          <input type="checkbox" name="'+rowJson.control_field_id+'"   lay-skin="primary" title="${item.'+rowJson.control_value
															+'}"   lay-filter="'+rowJson.control_field_id+'" class="layui-input" value="${item.'+rowJson.control_key+'}" >    ';
						inputHtml+='<%   ' ;
						inputHtml+='    }		';
						inputHtml+='%>   ';
						inputHtml+=' </div>  ';
						break;
					case "datetime": 
						//如果是时间格式  如果是间隔时间 <input type="text" class="layui-input" id="test1">
						inputHtml+='  <div class=" '+rowJson.control_layout+'_ext ">  ';
						inputHtml+='	<input type="text" class="layui-input" id="'+rowJson.control_field_id+'">  ' ; 
						inputHtml+=' </div>  '; 
						inputHtml+=' <script>  ';
						inputHtml+=' laydate.render({  ';
						inputHtml+='	  elem: "#'+rowJson.control_field_id+'"  ';
						inputHtml+=' 		 ,format: "'+rowJson.control_dateformat+'"   ';
						inputHtml+=' 		 ,range: '+rowJson.control_daterange+'   ';
						inputHtml+='});  ';
						inputHtml+=' </script>  ';
						break;
					case "button": 
						inputHtml+='  <div class=" '+rowJson.control_layout+'_ext "  >    <button id="'+rowJson.control_field_id+'"   name="'+rowJson.control_field_id+'"  placeholder="'+rowJson.control_label+'" autocomplete="off" class="layui-btn '+rowJson.control_style+'" > '+rowJson.control_label+' </button> </div>  ';		
						
						break;
					case "div":
							inputHtml+='  <div id="'+rowJson.control_field_id+'" class=" '+rowJson.control_layout+'_ext layui-form-item "  >    ';	 
						break;
					case "div_end": 
						inputHtml+='  </div>    ';	 
						 break; 
					case "form": 
						inputHtml+='  <form  id="'+rowJson.control_field_id+'"  name="'+rowJson.control_field_id+'">    ';	 
						 break; 
					case "form_end": 
						inputHtml+='  </form>    ';	 
						 break; 
					case "include": 
						inputHtml+='  <div class=" '+rowJson.control_layout+'_ext "> <%  include("'+rowJson.control_include+'",{data:data,paras:"",isajax:'+rowJson.control_isajax+'}){ }  %> </div>  ';	 
						break;	
					case "textarea":   
						inputHtml+='<div class="layui-input-block">   ';
						inputHtml+=' <textarea id="'+rowJson.control_field_id+'" placeholder="'+rowJson.control_label+'" class="layui-textarea"></textarea> ';
						inputHtml+='  </div>    ';
						break;
					case "image":  
						inputHtml+='<div class=" '+rowJson.control_layout+'_ext ">   ';
						inputHtml+='	<div class="layui-input-block">   ';
						inputHtml+='	 	<img class="layui-upload-img" id="'+rowJson.control_field_id+'"  > ';
						inputHtml+='	</div>    ';
						inputHtml+='</div>    ';
						break;
					case "upload":	
						//<button type="button" class="layui-btn" id="test3"><i class="layui-icon"></i>上传文件</button>
						inputHtml+='<div class=" '+rowJson.control_layout+'_ext ">   ';
						inputHtml+='	<div class="layui-input-block">   ';
						inputHtml+='		 <button type="button" class="layui-btn" id="'+rowJson.control_field_id+'"><i class="layui-icon"></i>'+rowJson.control_label+'</button> ';
						inputHtml+='	</div>    ';
						inputHtml+='</div>    ';
						break;
					case "pager":	
						//完整功能 
						var showPage='';
						if("1"==rowJson.control_pager_show){
							//显示分页
							 
								showPage+='   { 				';
									showPage+='    elem: "#'+rowJson.control_field_id+'"	';
									showPage+='    ,count: '+rowJson.control_count+'		';
									showPage+='    ,curr: '+rowJson.control_page+'		';
										showPage+='    ,layout: [';
											if(rowJson.control_pager_next){
												showPage+='   "next",';
											} 
											if(rowJson.control_pager_count){
												showPage+='   "count",';
											} 
											if(rowJson.control_pager_limit){
												showPage+='   "limit",';
											} 
											if(rowJson.control_pager_prev){
												showPage+='   "prev",';
											} 
											if(rowJson.control_pager_page){
												showPage+='   "page",';
											} 
											if(rowJson.control_pager_skip){
												showPage+='   "skip",';
											}  
										showPage+= '"" ]	';
										showPage+='    ,jump: function(obj){	';
											showPage+='      	';
												showPage+='    }	';
													showPage+='  }  	';
							 
						}else{
							//不显示分页
							showPage="false";    
						}

						inputHtml+= '      var jdata_'+rowJson.control_field_id+'=[];      ';
						inputHtml+= '     <%                      ';
						inputHtml+= '           var dataObject_'+rowJson.control_field_id+'=JavaScriptUtils.getJavaScript(data);  ';
						inputHtml+= '       %>                    ';
						inputHtml+= '                           ';
						inputHtml+= '     var jData_'+rowJson.control_field_id+'=${dataObject_'+rowJson.control_field_id+'};                     ';
						inputHtml+= '       jdata_'+rowJson.control_field_id+'=jData_'+rowJson.control_field_id+'.DATA;        ';
						
						inputHtml+= '    laytable.render({                                                  ';
						inputHtml+= '      elem: "#'+rowJson.control_field_id+'"                         ';
					
						
						inputHtml+= '      ,data:jdata_'+rowJson.control_field_id+'                                    ';
						inputHtml+= '      ,cols: [[                                                     ';
						inputHtml+= '         {field:"id", width:80, title: "ID", sort: true}             ';
						inputHtml+= '        ,{field:"username", width:80, title: "用户名", sort: true}              ';
						 inputHtml+= '     ]]                                                            ';
						inputHtml+= '      ,page: '+showPage+'  ';
						inputHtml+= '    });    ';
							 
						break;
					case "table":	
						 
						break;	
					case "texteditor":  
						break;	
				 }  
			
			var htmlContent=$("#control_html").val(); 
			htmlContent+=inputHtml;//生成的html
			$("#control_html").val(htmlContent);
			rowJson["control_html"]=htmlContent;
            options.controlDataJson[e_row.attr('data-value')] = rowJson; 
		 });
		
		 
		//换成 layui事件 
		layui_form.on('select(layout)', function(data){
			 var value = data.value; 
           rowJson[$("#control_layout").attr('id')] = value;
           options.controlDataJson[e_row.attr('data-value')] = rowJson; 
			 e_row.attr("class","lr-compont-item ui-draggable active "+value); 
		});
		 if(rowJson.control_isajax==undefined||rowJson.control_isajax==null){
			 rowJson.control_isajax=false;
		 }
        $html.find('#control_isajax').val(rowJson.control_isajax); 
        
        layui_form.on('checkbox(ajax)', function(data){
        	rowJson["control_isajax"] = data.elem.checked;
            options.controlDataJson[e_row.attr('data-value')] = rowJson; 
        });    
        
        
        if(rowJson.control_dateformat==undefined||rowJson.control_dateformat==null){
			 rowJson.control_dateformat='yyyy-MM-dd';
		 }
        $html.find('#control_dateformat').val(rowJson.control_dateformat); 
        
        
        if(rowJson.control_daterange==undefined||rowJson.control_daterange==null){
			 rowJson.control_daterange=false;
		 }
       $html.find('#control_daterange').val(rowJson.control_daterange); 
       
       
		$html.find('#control_include').val(rowJson.control_include);
		layui_form.on('select(include)', function(data){
			 var value = data.value; 
          rowJson[$("#control_include").attr('id')] = value;
          options.controlDataJson[e_row.attr('data-value')] = rowJson;  
		});
		
		layui_form.on('radio(control_buttontype)', function(data){
			 var value = data.value; 
         rowJson["control_buttontype"] = value;
         options.controlDataJson[e_row.attr('data-value')] = rowJson;  
		});
  
	   $html.find('#control_field_id').keyup(function (e) {
            var value = $(this).val(); 
            rowJson[$(this).attr('id')] = value;
            options.controlDataJson[e_row.attr('data-value')] = rowJson;
        });
        $html.find('#control_label').keyup(function (e) {
            var value = $(this).val();
            e_row.find('.lr-compont-title').find('span').html(html_encode(value));
            rowJson[$(this).attr('id')] = value;
            options.controlDataJson[e_row.attr('data-value')] = rowJson;
        }); 
        
        layui_form.on('select(verify)', function(data){
			 var value = data.value; 
			 rowJson[$("#control_verify").attr('id')] = value;
			 options.controlDataJson[e_row.attr('data-value')] = rowJson;   
		});
        
        $html.find('#control_default').keyup(function (e) {
            var value = $(this).val();
            rowJson[$(this).attr('id')] = value;
            options.controlDataJson[e_row.attr('data-value')] = rowJson;
        }); 
    } 
 
    return options;
}
 
//获取表单数据
$.fn.frmGetData = function () {
    var reVal = ""; var checkboxValue = {};
    $(this).find('input,select,textarea,.ui-select').each(function (r) {
        var id = $(this).attr('id');  
        if (id != undefined)
        {
            var filedid = id.replace('frm_', '');
            var type = $(this).attr('type');

            switch (type) {
                case "checkbox":
                    var datavalue = $("#" + id).attr('data-value');
                    var value = $("#" + id).val();
                    if ($("#" + id).is(":checked")) {
                        //reVal += '"' + filedid.replace(datavalue, '') + '"' + ':' + '"' + $.trim(value) + '",'
                        if (checkboxValue[filedid.replace(datavalue, '')] == undefined) {
                            checkboxValue[filedid.replace(datavalue, '')] = "";
                        }
                        else {
                            checkboxValue[filedid.replace(datavalue, '')] += '|';
                        }
                        checkboxValue[filedid.replace(datavalue, '')] += $.trim(value);
                    }
                    break;
                case "radio":
                    var datavalue = $("#" + id).attr('data-value');
                    var value = $("#" + id).val();
                    if ($("#" + id).is(":checked")) {
                        reVal += '"' + filedid.replace(datavalue, '') + '"' + ':' + '"' + $.trim(value) + '",'
                    }
                    break;
                case "select":
                    var value = $("#" + id).attr('data-value');
                    if (value == "") {
                        value = "&nbsp;";
                    }
                    reVal += '"' + filedid + '"' + ':' + '"' + $.trim(value) + '",'
                    break;
                case "selectTree":
                    var value = $("#" + id).attr('data-value');
                    if (value == "") {
                        value = "&nbsp;";
                    }
                    reVal += '"' + filedid + '"' + ':' + '"' + $.trim(value) + '",'
                    break;
                default:
                    var value = $("#" + id).val();
                    if (value == "") {
                        value = "&nbsp;";
                    }
                    reVal += '"' + filedid + '"' + ':' + '"' + $.trim(value) + '",'
                    break;
            }
        }
    });
    for (var i in checkboxValue)
    {
        reVal += '"' + i + '"' + ':' + '"' + checkboxValue[i] + '",'
    }

    reVal = reVal.substr(0, reVal.length - 1);
    reVal = reVal.replace(/\\/g, '\\\\');
    reVal = reVal.replace(/\n/g, '\\n');
    var postdata = jQuery.parseJSON('{' + reVal + '}');
     
    return postdata;
}
//设置表单数据
$.fn.frmSetData = function (data) { 
    var $id = $(this)
    for (var key in data) {
        var id = $id.find('#frm_' + key);
        var value = $.trim(data[key]).replace(/&nbsp;/g, '');
        if (id.attr('id')) {
            var type = id.attr('type');
            switch (type) {
                case "radio":
                    break;
                case "checkbox":
                    if (value == 1) {
                        id.attr("checked", 'checked');
                    } else {
                        id.removeAttr("checked");
                    }
                    break;
                case "select":
                    id.ComboBoxSetValue(value);
                    break;
                case "selectTree":
                    id.ComboBoxTreeSetValue(value);
                    break;
                default:
                    id.val(value);
                    break;
            }
        }
        else {
            $(this).find('input').each(function (r) {
                var checkid = $(this).attr('id');
                
                var checkfiledid = (undefined == checkid) ? '' : checkid.replace('frm_', '');
                var checktype = $(this).attr('type');
                var checkValue = $(this).val();
                switch (checktype) {
                    case "checkbox":
                        var datavalue = $(this).attr('data-value');
                        if ((key + datavalue) == checkfiledid )
                        {
                            var vlist = value.split('|');
                            for (var i in vlist)
                            {
                                if (vlist[i] == checkValue)
                                {
                                    $(this).attr("checked", 'checked');
                                    break;
                                }
                            }
                        }
                        break;
                    case "radio":
                        var datavalue = $(this).attr('data-value');
                        if ((key + datavalue) == checkfiledid && checkValue == value) {
                            $(this).attr("checked", 'checked');
                        }
                        break;
                    default:
                        break;
                }
            });
        }
    }
}
