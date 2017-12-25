/*author ZHU Xinyu 2016*/
/**
A javascript class which can return a transiting color value 
var reds= [255  , 0 , 255 , 0 , 0 , 0 , 255 ,255, 0, 255];
var greens=[255  , 0 , 0 , 0,  255 , 255 , 0 , 255];
var blues=[0  , 255 , 255 , 0 ,255  , 0  , 0 , 255,255];
var ColorTransition1=new  ColorTransition(reds,greens,blues);
ColorTransition1.getNextColor();//This will return the value
*/
function ColorTransition(redList,greenList,blueList){
        this.red= redList;
        this.green=greenList;
        this.blue= blueList;
        this.num=1;
        this.colorA={r:this.red[0],g:this.green[0],b:this.blue[0]};
        this.target={r:this.red[this.num%this.red.length],g:this.green[this.num%this.green.length],b:this.blue[this.num%this.blue.length]};


        this.componentToHex=function (c) {
            var hex = c.toString(16);
            return hex.length == 1 ? "0" + hex : hex;
        }

        this.rgbToHex=function(r, g, b) {
            return "#" + this.componentToHex(r) +this. componentToHex(g) + this.componentToHex(b);
        }


        this.closeTo=function (x,tx){
          if(x>tx) return x-1;
          if(x<tx) return x+1;
          return x;
        }
        this.adjustColor=function (color,dcolor){
          return {
                  r:this.closeTo(color.r,dcolor.r),
                  g:this.closeTo(color.g,dcolor.g),
                  b:this.closeTo(color.b,dcolor.b)
                };
        }
       this.equalColor= function (colora,colorb){
          return colora.r==colorb.r&&colora.g==colorb.g&&colora.b==colorb.b;
        }


        this.alertColor=function (){
          if(!this.equalColor(this.colorA,this.target)){
           this.colorA= this.adjustColor(this.colorA,this.target);
           return this.colorA;
          }else{
            this.num+=1;
             this.target={r:this.red[this.num%this.red.length],g:this.green[this.num%this.green.length],b:this.blue[this.num%this.blue.length]};
            return this.alertColor();
          }
        }

       this.getNextColor= function (){
          var next=this.alertColor();
          return this.rgbToHex(next.r,next.g,next.b);
        }
        //return a random color in hex
        this.getRandomColor=function()  {
            var letters = '0123456789ABCDEF'.split('');
            var color = '#';
            for (var i = 0; i < 6; i++ ) {
                color += letters[Math.floor(Math.random() * 16)];
            }
            return color;
        }
}