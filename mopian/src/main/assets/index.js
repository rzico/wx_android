// { "framework": "Vue" }

/******/
(function(modules) { // webpackBootstrap
    /******/ // The module cache
    /******/
    var installedModules = {};

    /******/ // The require function
    /******/
    function __webpack_require__(moduleId) {

        /******/ // Check if module is in cache
        /******/
        if (installedModules[moduleId])
        /******/
            return installedModules[moduleId].exports;

        /******/ // Create a new module (and put it into the cache)
        /******/
        var module = installedModules[moduleId] = {
            /******/
            exports: {},
            /******/
            id: moduleId,
            /******/
            loaded: false
                /******/
        };

        /******/ // Execute the module function
        /******/
        modules[moduleId].call(module.exports, module, module.exports, __webpack_require__);

        /******/ // Flag the module as loaded
        /******/
        module.loaded = true;

        /******/ // Return the exports of the module
        /******/
        return module.exports;
        /******/
    }


    /******/ // expose the modules object (__webpack_modules__)
    /******/
    __webpack_require__.m = modules;

    /******/ // expose the module cache
    /******/
    __webpack_require__.c = installedModules;

    /******/ // __webpack_public_path__
    /******/
    __webpack_require__.p = "";

    /******/ // Load entry module and return exports
    /******/
    return __webpack_require__(0);
    /******/
})
/************************************************************************/
/******/
([
    /* 0 */
    /***/
    function(module, exports, __webpack_require__) {

        'use strict';

        var _c11bfd774e471fc1cb9ef7b4b9c008d = __webpack_require__(1);

        var _c11bfd774e471fc1cb9ef7b4b9c008d2 = _interopRequireDefault(_c11bfd774e471fc1cb9ef7b4b9c008d);

        function _interopRequireDefault(obj) {
            return obj && obj.__esModule ? obj : {
                default: obj
            };
        }

        _c11bfd774e471fc1cb9ef7b4b9c008d2.default.el = '#root';
        new Vue(_c11bfd774e471fc1cb9ef7b4b9c008d2.default);

        /***/
    },
    /* 1 */
    /***/
    function(module, exports, __webpack_require__) {

        var __vue_exports__, __vue_options__
        var __vue_styles__ = []

        /* styles */
        __vue_styles__.push(__webpack_require__(2))

        /* script */
        __vue_exports__ = __webpack_require__(3)

        /* template */
        var __vue_template__ = __webpack_require__(4)
        __vue_options__ = __vue_exports__ = __vue_exports__ || {}
        if (
            typeof __vue_exports__.default === "object" ||
            typeof __vue_exports__.default === "function"
        ) {
            if (Object.keys(__vue_exports__).some(function(key) {
                    return key !== "default" && key !== "__esModule"
                })) {
                console.error("named exports are not supported in *.vue files.")
            }
            __vue_options__ = __vue_exports__ = __vue_exports__.default
        }
        if (typeof __vue_options__ === "function") {
            __vue_options__ = __vue_options__.options
        }
        __vue_options__.__file = "/usr/src/app/raw/c11bfd774e471fc1cb9ef7b4b9c008d2.vue"
        __vue_options__.render = __vue_template__.render
        __vue_options__.staticRenderFns = __vue_template__.staticRenderFns
        __vue_options__._scopeId = "data-v-02a9c8a8"
        __vue_options__.style = __vue_options__.style || {}
        __vue_styles__.forEach(function(module) {
            for (var name in module) {
                __vue_options__.style[name] = module[name]
            }
        })
        if (typeof __register_static_styles__ === "function") {
            __register_static_styles__(__vue_options__._scopeId, __vue_styles__)
        }

        module.exports = __vue_exports__


        /***/
    },
    /* 2 */
    /***/
    function(module, exports) {

        module.exports = {
            "isvisible": {
                "visibility": "visible"
            },
            "novisible": {
                "visibility": "hidden"
            },
            "userBox": {
                "flexDirection": "row",
                "alignItems": "center"
            },
            "nav": {
                "marginTop": 40,
                "flexDirection": "row",
                "height": 96,
                "width": 750,
                "alignItems": "center",
                "justifyContent": "space-between",
                "paddingRight": 30,
                "paddingLeft": 30
            },
            "headImg": {
                "height": 50,
                "width": 50,
                "borderRadius": 25
            },
            "navText": {
                "paddingLeft": 10,
                "fontSize": 33
            },
            "setTop": {
                "top": 136
            },
            "posFixed": {
                "position": "fixed",
                "backgroundColor": "#FF0000",
                "top": 136
            },
            "header": {
                "flexDirection": "row",
                "position": "fixed",
                "backgroundColor": "#ffffff",
                "left": 0,
                "right": 0,
                "top": 0,
                "height": 136
            },
            "@TRANSITION": {
                "navTransition-enter-active": {
                    "duration": 0
                },
                "navTransition-leave-active": {
                    "duration": 0
                },
                "paraTransition-enter-active": {
                    "duration": 200
                },
                "paraTransition-leave-active": {
                    "duration": 200
                }
            },
            "navTransition-enter-active": {
                "transitionDuration": 0
            },
            "navTransition-leave-active": {
                "transitionDuration": 0
            },
            "navTransition-leave-to": {
                "transform": "translateX(0px)",
                "opacity": 0
            },
            "navTransition-enter-to": {
                "transform": "translateX(0px)",
                "opacity": 1
            },
            "navTransition-enter": {
                "transform": "translateX(0px)",
                "opacity": 0
            },
            "paraTransition-enter-active": {
                "transitionDuration": 200
            },
            "paraTransition-leave-active": {
                "transitionDuration": 200
            },
            "paraTransition-leave-to": {
                "transform": "translateX(0px)",
                "opacity": 0
            },
            "paraTransition-enter-to": {
                "transform": "translateX(0px)",
                "opacity": 1
            },
            "paraTransition-enter": {
                "transform": "translateX(0px)",
                "opacity": 0
            },
            "rightBlur": {
                "right": 100,
                "width": 20
            },
            "leftBlur": {
                "left": 0
            },
            "blur": {
                "position": "absolute",
                "height": 79,
                "width": 20,
                "top": 0,
                "opacity": 0.7
            },
            "corpusBox": {
                "flexDirection": "row",
                "height": 80,
                "borderBottomWidth": 1,
                "borderStyle": "solid",
                "borderColor": "#DCDCDC",
                "backgroundColor": "#ffffff"
            },
            "redColor": {
                "color": "#D9141E"
            },
            "rightHiddenIconBox": {
                "justifyContent": "center",
                "alignItems": "center"
            },
            "rightHiddenSmallBox": {
                "flexDirection": "row",
                "flex": 1,
                "justifyContent": "space-around",
                "alignItems": "center"
            },
            "rightHiddenText": {
                "fontSize": 24,
                "color": "#999999"
            },
            "rightHiddenIcon": {
                "textAlign": "center",
                "lineHeight": 90,
                "fontSize": 40,
                "width": 90,
                "height": 90,
                "borderRadius": 45,
                "color": "#000000",
                "backgroundColor": "#ffffff",
                "marginBottom": 15
            },
            "rightHidden": {
                "position": "absolute",
                "right": 0,
                "top": 0,
                "backgroundColor": "#f4f4f4",
                "width": 330,
                "height": 457
            },
            "relevantImage": {
                "flexDirection": "row",
                "fontSize": 32,
                "color": "#888888",
                "marginRight": 5,
                "marginLeft": 5,
                "alignItems": "center"
            },
            "relevantText": {
                "color": "#888888",
                "fontSize": 26
            },
            "relevantInfo": {
                "flexDirection": "row",
                "alignItems": "center"
            },
            "articleFoot": {
                "flexDirection": "row",
                "justifyContent": "space-between",
                "width": 690,
                "alignItems": "center"
            },
            "articleDate": {
                "fontSize": 24,
                "color": "#888888"
            },
            "articleCover": {
                "height": 300,
                "width": 690,
                "borderRadius": 5,
                "marginTop": 30,
                "marginBottom": 30
            },
            "articleBox": {
                "backgroundColor": "#ffffff",
                "paddingLeft": 30,
                "paddingTop": 30,
                "paddingRight": 30,
                "paddingBottom": 30,
                "marginBottom": 10,
                "width": 1080,
                "display": "inline-block"
            },
            "atricleHead": {
                "flexDirection": "row",
                "alignItems": "center"
            },
            "articleTitle": {
                "fontSize": 32,
                "marginLeft": 10
            },
            "articleSign": {
                "borderRadius": 10,
                "paddingTop": 5,
                "paddingRight": 5,
                "paddingBottom": 5,
                "paddingLeft": 5,
                "color": "#888888",
                "fontSize": 26,
                "borderWidth": 1,
                "borderStyle": "solid",
                "borderColor": "#DCDCDC"
            },
            "indicator": {
                "width": 750,
                "height": 80,
                "textAlign": "center",
                "lineHeight": 80
            },
            "wrapper": {
                "backgroundColor": "#f4f4f4"
            },
            "tipsText": {
                "color": "#808080",
                "fontSize": 26,
                "marginTop": 240,
                "paddingBottom": 200
            },
            "active": {
                "color": "#F0AD3C",
                "borderColor": "#F0AD3C",
                "borderStyle": "solid",
                "borderBottomWidth": 4
            },
            "noActive": {
                "borderBottomWidth": 0
            },
            "articleClass": {
                "flexDirection": "row",
                "paddingLeft": 10,
                "borderBottomWidth": 1,
                "borderStyle": "solid",
                "borderColor": "#DCDCDC",
                "height": 80,
                "backgroundColor": "#ffffff"
            },
            "allArticle": {
                "fontSize": 29,
                "lineHeight": 80,
                "paddingLeft": 20,
                "paddingRight": 20
            },
            "topBtnBorder": {
                "position": "absolute",
                "height": 40,
                "top": 20,
                "right": 0,
                "backgroundColor": "#000000",
                "borderStyle": "solid",
                "borderColor": "#ffffff",
                "borderRightWidth": 1
            },
            "backgroundImage": {
                "position": "absolute",
                "width": 750,
                "top": 0,
                "height": 420,
                "filter": "blur(4px)",
                "opacity": 0.8
            },
            "topBox": {
                "position": "relative",
                "paddingTop": 40,
                "height": 420,
                "backgroundColor": "#D9141E"
            },
            "topBtnBox": {
                "flexDirection": "row",
                "alignItems": "center",
                "marginTop": 40,
                "width": 500,
                "marginLeft": 125
            },
            "topBtnSmallBox": {
                "height": 80,
                "flex": 1
            },
            "topBtn": {
                "color": "#FFFFFF",
                "fontSize": 24,
                "textAlign": "center",
                "height": 40,
                "lineHeight": 40
            },
            "topMoneySize": {
                "fontWeight": "400",
                "fontSize": 32
            },
            "topBtnBigFont": {
                "fontWeight": "400",
                "fontSize": 38
            },
            "topHead": {
                "flexDirection": "column",
                "alignItems": "center",
                "paddingTop": 20,
                "color": "#FFFFFF"
            },
            "testImage": {
                "width": 120,
                "height": 120,
                "borderRadius": 60
            },
            "userSign": {
                "lines": 1,
                "textOverflow": "ellipsis",
                "width": 500,
                "fontSize": 26,
                "color": "#FFFFFF"
            },
            "userName": {
                "fontWeight": "600",
                "fontSize": 32,
                "marginTop": 15,
                "marginBottom": 15,
                "color": "#FFFFFF"
            }
        }

        /***/
    },
    /* 3 */
    /***/
    function(module, exports) {

        'use strict';

        Object.defineProperty(exports, "__esModule", {
            value: true
        });
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //

        //    import {dom,event} from '../../weex.js';
        var dom = weex.requireModule('dom');
        var event = weex.requireModule('event');
        var modal = weex.requireModule('modal');
        var animation = weex.requireModule('animation');
        //    import utils from '../../assets/utils';
        //    import { POST, GET } from '../../assets/fetch'
        var animationPara; //执行动画的文章
        var scrollTop = 0;
        var recycleScroll = 0;
        var allArticleScroll = 0;
        exports.default = {
            data: function data() {
                return {
                    testaaa: 'https://img.alicdn.com/tps/TB1z.55OFXXXXcLXXXXXXXXXXXX-560-560.jpg',
                    settingColor: 'white',
                    opacityNum: 0,
                    twoTop: false,
                    isDisappear: false,
                    corpusPosition: 'relative',
                    corpusScrollTop: 0,
                    canScroll: true,
                    userName: '刮风下雨打雷台风天',
                    userSign: '刮风下雨打雷台风天。刮风下雨打雷台风天。刮风下雨打雷台风天。刮风下雨打雷台风天。刮风下雨打雷台风天。刮风下雨打雷台风天。',
                    whichCorpus: 0,
                    isNoArticle: false,
                    refreshing: 'hide',
                    fontName: '&#xe685;',
                    collectNum: 0,
                    moneyNum: 0,
                    focusNum: 0,
                    imageUrl: 'https://gd3.alicdn.com/bao/uploaded/i3/TB1x6hYLXXXXXazXVXXXXXXXXXX_!!0-item_pic.jpg',
                    id: '334',
                    showLoading: 'hide',
                    //                imageUrl: 'https://img.alicdn.com/tps/TB1z.55OFXXXXcLXXXXXXXXXXXX-560-560.jpg',

                    corpusList: [{
                        name: '全部文章',
                        id: ''
                    }, {
                        name: '回收站',
                        id: '99'
                    }],

                    //                全部文章==================
                    articleList: []
                };
            },
            computed: {
                positionObject: function positionObject() {
                    return {
                        //                    top:this.corpusScrollTop + 'px',
                        position: this.corpusPosition
                    };
                }
            },
            mounted: function mounted() {
                //            let domModule = weex.requireModule('dom');
                //            domModule.addRule('fontFace', {
                //                'fontFamily': "iconfont",
                //                'src': "url('"+resLocateURL+"resources/fonts/iconfont.ttf')"
                //            });
            },

            created: function created() {
                //            utils.initIconFont();
                var _this = this;
                //            获取文集列表
                //            GET('/weex/member/article_catalog/list.jhtml')
                //                .then(
                //                    function (data) {
                //                        if (data.type == "success") {
                //                            if(data.data == ''){
                //                            }else{
                //                                event.toast(data.data[2].name);
                ////                                将文集名循环插入数组中
                //                                for(let i = 0; i<data.data.length;i++){
                //                                    _this.corpusList.splice(1 + i,0,data.data[i]);
                //                                }
                //                            }
                //                        } else {
                //                            event.toast(data.content);
                //                        }
                //                    },
                //                    function (err) {
                //                        event.toast("网络不稳定")
                //                    }
                //                )

                var options = {
                    type: 'article',
                    keyword: '',
                    orderBy: 'desc',
                    current: 0,
                    pageSize: 10
                };
                event.findList(options, function(data) {
                    if (data.type == "success" && data.data != '') {
                        var sss = "";
                        // _this.userName = data.data;
                        data.data.forEach(function(item) {
                            //                        event.toast(item);
                            //                    将value json化
                            sss += item.value;
                            item.value = JSON.parse(item.value);
                            //                        把读取到的文章push进去文章列表
                            _this.articleList.push(item);
                        _this.userName = sss;
                        });
                    } else {
                        event.toast('缓存' + data.content);
                    }
                });

                //            let option = {
                //                type:'arcticle',//类型
                //                keyword:'N',//关键址
                //                orderBy:'desc',//"desc"降序 ,"asc"升序
                //                current:'0', //当前有几页
                //                pageSize:'10' //一页显示几行
                //            }
                //            event.findList(option,function (message) {
                //                event.toast(message);
                //                if(message.type == 'success' && message.data != ''){
                //
                //                }
                //            })
            },
            methods: {
                jumpEditor: function jumpEditor(id) {
                    event.toast('跳转编辑');
                    var _this = this;
                    //                event.openURL(utils.locate('view/member/editor/editor.js?articleId=' + id),function (message) {
                    //                    _this.updateArticle();
                    //                });

                    //                event.openURL('http://192.168.2.157:8081/editor.weex.js?articleId=' + id,function () {
                    ////                    _this.updateArticle();
                    //                })
                },
                jumpDelete: function jumpDelete() {
                    event.toast('文章删除');
                },
                jumpTop: function jumpTop() {
                    event.toast('文章置顶');
                },
                jumpCorpus: function jumpCorpus() {
                    event.toast('跳转文集');
                },
                //            open (callback) {
                //                return stream.fetch({
                //                    method: 'GET',
                //                    type: 'json',
                //                    url: '/weex/member/article/list.jhtml'
                //                }, callback)
                //            },
                //            switchArticle:function (item) {
                //                if(this.whichCorpus == item || this.whichCorpus == '全部文章'){
                //                    return true;
                //                }else{
                //                    return false;
                //                }
                //                if(this.isAllArticle == false){
                //                    if(item.articleSign == '已删除'){
                //                        return true;
                //                    }else{
                //                        return false;
                //                    }
                //                }else{
                //                    return true;
                //                }
                //            },
                //            前往文章
                goArticle: function goArticle(id) {
                    var _this = this;
                    //                event.openURL(utils.locate('view/member/editor/editor.js?articleId=' + id),function (message) {
                    ////                    _this.updateArticle();
                    //                });
                    event.openURL('http://192.168.2.157:8081/editor.weex.js?articleId=' + id, function() {
                        //                    _this.updateArticle();
                    });
                },

                //            updateArticle(){
                //                var _this = this;
                ////            获取文章缓存。
                //                event.findList(1,'articleListTest1','desc',function (data) {
                ////                    modal.toast({message:data.data});
                //                    if(data.type == 'success'){
                //                        for(let i = 0;i < data.data.length;i++){
                //                            let articleData = JSON.parse(data.data[i].value);
                //                            _this.articleList.splice(0,0,{
                //                                articleSign: '草稿',
                //                                articleTitle:   articleData[0].title,
                //                                articleCoverUrl:  articleData[0].thumbnail,
                //                                articleDate: '2017-09-23',
                //                                browse: 0,
                //                                praise: 0,
                //                                comments: 0,
                //                                id:articleData[0].id,
                //                            })
                //                        }
                //                    }else{
                //                        modal.alert({
                //                            message: data.content,
                //                            duration: 0.3
                //                        })
                //                    }
                //                })
                //            },
                //            toPage: function(url){
                ////                event.pageTo(url, false);
                //                event.wxConfig(function (data) {
                //                    event.showToast(data.color);
                //                });
                //            },
                jump: function jump(vueName) {
                    event.toast('will jump');
                },
                corpusChange: function corpusChange(index, id) {
                    event.toast(index);
                    var _this = this;
                    _this.whichCorpus = index;

                    //                if(this.isAllArticle == true){
                    //
                    //                }else{
                    //                    this.isAllArticle = true;
                    //                    recycleScroll = scrollTop;
                    //                    setTimeout(function () {
                    //
                    //                        if(allArticleScroll > 424){
                    //                            let listHeight = allArticleScroll - 424;
                    //                            let positionIndex =parseInt( listHeight / 457);
                    //                            let offsetLength = - listHeight % 457;
                    //                            modal.toast({message:"positionIndex" + positionIndex + "offsetLength" + offsetLength})
                    //                            const el = _this.$refs.animationRef[positionIndex]//跳转到相应的cell
                    //                            dom.scrollToElement(el, {
                    //                                animated:false,
                    //                                offset:  -80 - offsetLength
                    //
                    //                            })
                    //                        }
                    //                    },50)
                    //                }
                },
                //废弃
                //            recycleSite:function(){
                //                var _this = this;
                //                if(this.isAllArticle == false){
                //                    modal.toast({message:"相等"})
                //                }else{
                //                    this.isAllArticle = false;
                //                    allArticleScroll = scrollTop;
                //                    setTimeout(function () {
                //
                //                        if(recycleScroll > 424){
                //                            let listHeight = recycleScroll - 424;
                //                            let positionIndex =parseInt( listHeight / 457);
                //                            let offsetLength = - listHeight % 457;
                //                            modal.toast({message:"positionIndex" + positionIndex + "offsetLength" + offsetLength})
                //                            const el = _this.$refs.animationRef[positionIndex]//跳转到相应的cell
                //                            dom.scrollToElement(el, {
                //                                animated:false,
                //                                offset:  -80 - offsetLength
                //                            })
                //                        }
                //                    },50)
                //                }
                //
                //            },
                swipeHappen: function swipeHappen(event) {
                    console.log(event);
                    //                console.log(event.direction);
                    //                if(event.direction == 'left'){
                    //                    this.isAllArticle = false;
                    //                }else if(event.direction == 'right'){
                    //                    this.isAllArticle = true;
                    //                }
                },
                //            点击屏幕时
                ontouchstart: function ontouchstart(event, index) {
                    var _this = this;
                    if (animationPara == null || animationPara == '' || animationPara == 'undefinded') {} else {
                        animation.transition(animationPara, {
                            styles: {
                                transform: 'translateX(0)'
                            },
                            duration: 350, //ms
                            timingFunction: 'ease-in-out', //350 duration配合这个效果目前较好
                            //                      timingFunction: 'ease-out',
                            needLayout: false,
                            delay: 0 //ms
                        });
                    }
                    //                获取当前点击的元素。
                    animationPara = event.currentTarget;
                    //                canScroll 控制页面是否可以上下滑动
                    this.canScroll = true;
                },
                //            移动时
                onpanmove: function onpanmove(event, index) {
                    var _this = this;
                    if (event.direction == 'right') {
                        _this.canScroll = false;
                        animation.transition(animationPara, {
                            styles: {
                                transform: 'translateX(0)'
                            },
                            duration: 350, //ms
                            timingFunction: 'ease-in-out', //350 duration配合这个效果目前较好
                            //                      timingFunction: 'ease-out',
                            needLayout: false,
                            delay: 0 //ms
                        });
                    } else if (event.direction == 'left') {
                        _this.canScroll = false;
                        //                  modal.toast({message:distance});
                        animation.transition(animationPara, {
                            styles: {
                                transform: 'translateX(-330)'
                            },
                            duration: 350, //ms
                            timingFunction: 'ease-in-out', //350 duration配合这个效果目前较好
                            //                      timingFunction: 'ease-out',
                            needLayout: false,
                            delay: 0 //ms
                        });
                    }
                },
                onpanend: function onpanend(event) {},
                onloading: function onloading(event) {
                    var _this2 = this;

                    modal.toast({
                        message: '加载中...',
                        duration: 1
                    });
                    this.showLoading = 'show';
                    setTimeout(function() {
                        //                    const length = this.articleList.length
                        //                    for (let i = length; i < length + 2; ++i) {
                        //                        this.articleList.push({
                        //                            articleSign: '公开',
                        //                            articleTitle: '美丽厦门' + i,
                        //                            articleCoverUrl: 'https://img.alicdn.com/tps/TB1z.55OFXXXXcLXXXXXXXXXXXX-560-560.jpg',
                        //                            articleDate: '2017-09-01',
                        //                            browse: 626 + i,
                        //                            praise: 47 + i,
                        //                            comments: 39 + i
                        //                        })
                        //                    }
                        _this2.showLoading = 'hide';
                    }, 1500);
                },

                scrollHandler: function scrollHandler(e) {
                    var _this = this;
                    //                this.offsetX = e.contentOffset.x;
                    //                this.offsetY = e.contentOffset.y;
                    if (e.contentOffset.y >= 0) {
                        return;
                    }
                    scrollTop = Math.abs(e.contentOffset.y);
                    //                modal.toast({message:scrollTop});8

                    var opacityDegree = Math.floor(scrollTop / 14) / 10;
                    //                modal.toast({message:opacityDegree,duration:0.1});
                    if (opacityDegree > 1) {
                        opacityDegree = 1;
                    }
                    if (opacityDegree > 0.4) {
                        event.changeWindowsBar("true");
                        this.settingColor = 'black';
                    } else {
                        this.settingColor = 'white';
                        event.changeWindowsBar("false");
                    }
                    this.opacityNum = opacityDegree;

                    //                if(scrollTop >=284){
                    if (scrollTop >= 284) {
                        this.twoTop = true;
                        //                    this.corpusScrollTop = 136;
                        //                    this.corpusPosition = 'fixed';
                        //                    modal.toast({message:this.corpusPosition,duration:1})
                    } else {
                        this.twoTop = false;
                        //                     _this.corpusScrollTop = 420 -  scrollTop
                        //                    this.corpusPosition = 'relative';
                        //                    modal.toast({message:this.corpusScrollTop,duration:1})
                    }
                    if (scrollTop < 424) {
                        recycleScroll = 0;
                        allArticleScroll = 0;
                    }
                },
                //            ondisappear(){
                //              modal.toast({message:'消失',duration:1});
                ////                    this.corpusScrollTop = 0;
                //                    this.corpusPosition = 'fixed';
                //                    this.isDisappear = true;
                //            },
                //            onappear(){
                //                modal.toast({message:'显示',duration:1});
                //                this.isDisappear = false;
                //                this.corpusPosition = 'relative';
                //            },
                //            文集
                goCorpus: function goCorpus() {
                    //                event.openURL(utils.locate('view/member/editor/corpus.js'),
                    //                    function (data) {
                    //                        return ;
                    //                    });
                },

                //            个人信息
                goAttribute: function goAttribute() {
                    //                event.openURL(utils.locate('view/member/attribute.js'),
                    //                    function (data) {
                    //                        return ;
                    //                    }
                    //                );
                },

                //            设置中心
                goManage: function goManage() {
                    //                event.openURL(utils.locate('view/member/manage.js'),
                    //                    function (data) {
                    //                        return ;
                    //                    }
                    //                );
                },

                //            快速滑动滚动条时， 控制顶部导航栏消失
                toponappear: function toponappear() {
                    this.opacityNum = 0;
                    this.settingColor = 'white';
                    event.changeWindowsBar("false");
                }
            }
        };

        /***/
    },
    /* 4 */
    /***/
    function(module, exports) {

        module.exports = {
            render: function() {
                var _vm = this;
                var _h = _vm.$createElement;
                var _c = _vm._self._c || _h;
                return _c('div', {
                    staticClass: ["wrapper"],
                    attrs: {
                        "showScrollbar": "false",
                        "offsetAccuracy": "0",
                        "scrollable": _vm.canScroll
                    },
                    on: {
                        "scroll": _vm.scrollHandler
                    }
                }, [_c('div', {
                    staticStyle: {
                        position: "absolute",
                        top: "0",
                        left: "0",
                        width: "1px",
                        height: "1px",
                        opacity: "0"
                    },
                    on: {
                        "appear": _vm.toponappear
                    }
                }), _c('div', {
                    staticClass: ["header"],
                    class: [_vm.opacityNum == 0 ? 'novisible' : 'isvisible'],
                    style: {
                        opacity: _vm.opacityNum
                    }
                }, [_c('div', {
                    staticClass: ["nav"]
                }, [_c('div', {
                    staticStyle: {
                        width: "50px"
                    }
                }), (_vm.settingColor == 'black') ? _c('div', {
                    staticClass: ["userBox"],
                    on: {
                        "click": function($event) {
                            _vm.goAttribute()
                        }
                    }
                }, [_c('image', {
                    staticClass: ["headImg"],
                    attrs: {
                        "src": _vm.imageUrl
                    }
                }), _c('text', {
                    staticClass: ["navText"]
                }, [_vm._v(_vm._s(_vm.userName))])]) : _vm._e(), _c('div', {
                    staticStyle: {
                        width: "50px"
                    }
                })])]), _c('div', {
                    staticStyle: {
                        position: "fixed",
                        top: "63px",
                        right: "30px"
                    },
                    on: {
                        "click": function($event) {
                            _vm.goManage()
                        }
                    }
                }, [_c('text', {
                    staticStyle: {
                        fontSize: "50px"
                    },
                    style: {
                        fontFamily: 'iconfont',
                        color: _vm.settingColor
                    }
                }, [_vm._v("")])]), _c('div', {
                    staticClass: ["corpusBox"],
                    class: [_vm.twoTop ? 'isvisible' : 'novisible'],
                    staticStyle: {
                        top: "136px",
                        position: "fixed"
                    }
                }, [_c('scroller', {
                    staticStyle: {
                        flexDirection: "row",
                        width: "650px"
                    },
                    attrs: {
                        "scrollDirection": "horizontal"
                    }
                }, [_c('div', {
                    staticClass: ["articleClass"]
                }, _vm._l((_vm.corpusList), function(item, index) {
                    return _c('text', {
                        staticClass: ["allArticle"],
                        class: [_vm.whichCorpus == index ? 'active' : 'noActive'],
                        on: {
                            "click": function($event) {
                                _vm.corpusChange(index, item.id)
                            }
                        }
                    }, [_vm._v(_vm._s(item.name))])
                }))]), _c('div', {
                    staticStyle: {
                        width: "100px",
                        justifyContent: "center",
                        alignItems: "center",
                        backgroundColor: "white"
                    },
                    on: {
                        "click": function($event) {
                            _vm.goCorpus()
                        }
                    }
                }, [_c('text', {
                    staticStyle: {
                        fontSize: "35px"
                    },
                    style: {
                        fontFamily: 'iconfont'
                    }
                }, [_vm._v("")])])]), _c('scroller', {
                    ref: "topBox",
                    staticClass: ["topBox"]
                }, [_c('image', {
                    staticClass: ["backgroundImage"],
                    attrs: {
                        "src": _vm.imageUrl
                    }
                }), _c('div', {
                    staticClass: ["topHead"]
                }, [_c('image', {
                    staticClass: ["testImage"],
                    attrs: {
                        "src": _vm.imageUrl
                    }
                }), _c('div', {
                    staticStyle: {
                        alignItems: "center"
                    },
                    on: {
                        "click": function($event) {
                            _vm.goAttribute()
                        }
                    }
                }, [_c('text', {
                    staticClass: ["userName"]
                }, [_vm._v(_vm._s(_vm.userName))]), _c('text', {
                    staticClass: ["userSign"]
                }, [_vm._v(_vm._s(_vm.userSign))])])]), _c('div', {
                    staticClass: ["topBtnBox"]
                }, [_c('div', {
                    staticClass: ["topBtnSmallBox"],
                    on: {
                        "click": function($event) {
                            _vm.jump()
                        }
                    }
                }, [_c('div', {
                    staticClass: ["topBtnBorder"]
                }), _c('text', {
                    staticClass: ["topBtn", "topBtnBigFont"]
                }, [_vm._v(_vm._s(_vm.collectNum))]), _c('text', {
                    staticClass: ["topBtn"]
                }, [_vm._v("收藏")])]), _c('div', {
                    staticClass: ["topBtnSmallBox"],
                    on: {
                        "click": function($event) {
                            _vm.jump()
                        }
                    }
                }, [_c('div', {
                    staticClass: ["topBtnBorder"]
                }), _c('div', {
                    staticStyle: {
                        flexDirection: "row",
                        justifyContent: "center"
                    }
                }, [(_vm.moneyNum != 0) ? _c('text', {
                    staticClass: ["topBtn", "topMoneySize"]
                }, [_vm._v("¥ ")]) : _vm._e(), _c('text', {
                    staticClass: ["topBtn", "topBtnBigFont"]
                }, [_vm._v(_vm._s(_vm.moneyNum))])]), _c('text', {
                    staticClass: ["topBtn"]
                }, [_vm._v("钱包")])]), _c('div', {
                    staticClass: ["topBtnSmallBox"]
                }, [_c('text', {
                    staticClass: ["topBtn", "topBtnBigFont"]
                }, [_vm._v(_vm._s(_vm.focusNum))]), _c('text', {
                    staticClass: ["topBtn"]
                }, [_vm._v("关注")])])])]), _c('div', [_c('div', {
                    staticClass: ["corpusBox"],
                    style: _vm.positionObject
                }, [_c('scroller', {
                    staticStyle: {
                        flexDirection: "row",
                        width: "650px"
                    },
                    attrs: {
                        "scrollDirection": "horizontal"
                    }
                }, [_c('div', {
                    staticClass: ["articleClass"]
                }, _vm._l((_vm.corpusList), function(item, index) {
                    return _c('text', {
                        staticClass: ["allArticle"],
                        class: [_vm.whichCorpus == index ? 'active' : 'noActive'],
                        on: {
                            "click": function($event) {
                                _vm.corpusChange(index, item.id)
                            }
                        }
                    }, [_vm._v(_vm._s(item.name))])
                }))]), _c('div', {
                    staticStyle: {
                        width: "100px",
                        justifyContent: "center",
                        alignItems: "center",
                        backgroundColor: "white"
                    },
                    on: {
                        "click": function($event) {
                            _vm.goCorpus()
                        }
                    }
                }, [_c('text', {
                    staticStyle: {
                        fontSize: "35px"
                    },
                    style: {
                        fontFamily: 'iconfont'
                    }
                }, [_vm._v("")])])]), _c('div', [_c('transition-group', {
                    attrs: {
                        "name": "paraTransition",
                        "tag": "div"
                    }
                }, _vm._l((_vm.articleList), function(item, index) {
                    return _c('div', {
                        key: index,
                        staticClass: ["articleBox"],
                        on: {
                            "click": function($event) {
                                _vm.goArticle(item.key)
                            },
                            "touchstart": function($event) {
                                _vm.ontouchstart($event, index)
                            },
                            "swipe": function($event) {
                                _vm.onpanmove($event, index)
                            }
                        }
                    }, [_c('div', {
                        staticClass: ["atricleHead"]
                    }, [_c('text', {
                        staticClass: ["articleSign"]
                    }, [_vm._v("公开")]), _c('text', {
                        staticClass: ["articleTitle"]
                    }, [_vm._v(_vm._s(item.value.title))])]), _c('div', [_c('image', {
                        staticClass: ["articleCover"],
                        attrs: {
                            "src": item.value.thumbnail,
                            "resize": "cover"
                        }
                    })]), _c('div', {
                        staticClass: ["articleFoot"]
                    }, [_c('div', [_c('text', {
                        staticClass: ["articleDate"]
                    }, [_vm._v("2017-09-01")])]), (item.articleSign != '样例') ? _c('div', {
                        staticClass: ["relevantInfo"]
                    }, [_c('text', {
                        staticClass: ["relevantImage"],
                        style: {
                            fontFamily: 'iconfont'
                        }
                    }, [_vm._v("")]), _c('text', {
                        staticClass: ["relevantText"]
                    }, [_vm._v(_vm._s(item.value.hits))]), _c('text', {
                        staticClass: ["relevantImage"],
                        staticStyle: {
                            paddingBottom: "2px"
                        },
                        style: {
                            fontFamily: 'iconfont'
                        }
                    }, [_vm._v("")]), _c('text', {
                        staticClass: ["relevantText"]
                    }, [_vm._v(_vm._s(item.value.laud))]), _c('text', {
                        staticClass: ["relevantImage"],
                        style: {
                            fontFamily: 'iconfont'
                        }
                    }, [_vm._v("")]), _c('text', {
                        staticClass: ["relevantText"]
                    }, [_vm._v(_vm._s(item.value.review))])]) : _vm._e()]), _c('div', {
                        staticClass: ["rightHidden"]
                    }, [_c('div', {
                        staticClass: ["rightHiddenSmallBox"]
                    }, [_c('div', {
                        staticClass: ["rightHiddenIconBox"],
                        on: {
                            "click": function($event) {
                                _vm.jumpEditor(item.key)
                            }
                        }
                    }, [_c('text', {
                        staticClass: ["rightHiddenIcon"],
                        style: {
                            fontFamily: 'iconfont'
                        }
                    }, [_vm._v("")]), _c('text', {
                        staticClass: ["rightHiddenText"]
                    }, [_vm._v("编辑")])]), _c('div', {
                        staticClass: ["rightHiddenIconBox"],
                        on: {
                            "click": function($event) {
                                _vm.jumpDelete()
                            }
                        }
                    }, [_c('text', {
                        staticClass: ["rightHiddenIcon", "redColor"],
                        style: {
                            fontFamily: 'iconfont'
                        }
                    }, [_vm._v("")]), _c('text', {
                        staticClass: ["rightHiddenText", "redColor"]
                    }, [_vm._v("删除")])])]), _c('div', {
                        staticClass: ["rightHiddenSmallBox"]
                    }, [_c('div', {
                        staticClass: ["rightHiddenIconBox"],
                        on: {
                            "click": function($event) {
                                _vm.jumpTop()
                            }
                        }
                    }, [_c('text', {
                        staticClass: ["rightHiddenIcon"],
                        style: {
                            fontFamily: 'iconfont'
                        }
                    }, [_vm._v("")]), _c('text', {
                        staticClass: ["rightHiddenText"]
                    }, [_vm._v("置顶")])]), _c('div', {
                        staticClass: ["rightHiddenIconBox"],
                        on: {
                            "click": function($event) {
                                _vm.jumpCorpus()
                            }
                        }
                    }, [_c('text', {
                        staticClass: ["rightHiddenIcon"],
                        style: {
                            fontFamily: 'iconfont'
                        }
                    }, [_vm._v("")]), _c('text', {
                        staticClass: ["rightHiddenText"]
                    }, [_vm._v("文集")])])])])])
                }))], 1)]), _c('loading', {
                    staticClass: ["loading"],
                    attrs: {
                        "display": _vm.showLoading
                    },
                    on: {
                        "loading": _vm.onloading
                    }
                }, [_c('text', {
                    staticClass: ["indicator"]
                }, [_vm._v("Loading ...")])])])
            },
            staticRenderFns: []
        }
        module.exports.render._withStripped = true

        /***/
    }
    /******/
]);