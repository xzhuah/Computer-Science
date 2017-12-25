<%@ page contentType="text/html; charset=utf-8" language="java" import="java.net.URL,java.util.*,com.hkust.comp.*,java.io.IOException,com.hkust.comp.pageRelation.*,com.hkust.comp.urlPage.*,com.hkust.comp.urlword.*,com.hkust.comp.wordLib.*,com.hkust.comp.pagerank.*,org.htmlparser.beans.StringBean,java.net.*,java.io.*" errorPage="" %>


<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<title>Search Result</title>
<script src="https://ajax.googleapis.com/ajax/libs/jquery/1.12.0/jquery.min.js"></script> <!-- JQuery-->
<script src="./js/color.js" charset="utf-8"></script>

<link href="css/bootstrap.min.css" rel="stylesheet">
<link href="css/custom2.css" rel="stylesheet">
<link rel="stylesheet" href="css/style.css" type="text/css">
<link rel="icon" href="./img/searchIcon.png" type="image/x-icon">

</head>

<body>
<header>


<!-- navigation bar -->
<header>
    <nav>
      <form method="get" action="#">
            <div  class="navBar">
          <img src="img/search.png" height="80px" />
          <%
            String usrinput="" ;
            try{
             if(request.getParameter("search")!=null){
              usrinput=request.getParameter("search").replace("\'","\""); 
             }
         }catch(Exception e){}

           %>
<input type="text" class="css-input" id="input" name="search" value=<% out.println("\'"+usrinput+"\'"); %> />

<button class="myButton" type="submit" ><i class="fa fa-search">Quick Search</i></button>
<input type="checkbox" name="detail" value="true" class="check"

<% if(request.getParameter("detail")!=null) out.print("checked");%>
><span class="checkBox">Show Profile</span>


            
        <a href="#" class="navButton"><img class="navImage" id="help" src="./img/ask.png" width="30px" title="Help"/></a>
        <a href="#" class="navButton"><img class="navImage" id="tool" src="./img/tool.png" width="30px" title="Tools"/></a>
        <a href="#" class="navButton"><img class="navImage" id="menu" src="./img/menu.png" width="30px" title="Menu"/></a>
        <a href="#" class="myButton2" id="sign">Sign In</a>
       
        </div>
        </form>  
    </nav>
    <div class="Help  top">
    <h3>Quick Guide On Using Searching Engine</h3>
    <p>You can type in keyword in our input field and click on Quick Search, we will return you a list of page which are relavent to the keyword you inputed and are sorted by the similarity. <br><br>
    Our system will rank different words in your query by the frequency. For example, if you want to search relational database and you think database is more important than relational, you can input relational database database. <br><br>
    Check "Show Profile" If you want to see more information about the result links before enter it. But this may slow down the search speed a little bit. <br><br />
    We also show you the Parent links and Child Link of each page, just click on the tag, the detailed information will appear.
    </p>
    </div>
    <div class="Tools top">
    <h3>Most Popular Word Prefix:</h3>
    <% 
    out.println(Tools.getAllWord(0.3,0.2));
    %>
   <!-- <ul>
    <li><a href="https://www.google.com">Google Search</a></li>
    <li><a href="https://www.baidu.com">Baidu Search</a></li>
     <li><a href="https://www.bing.com/">Bing Search</a></li>
     <li><a href="https://hk.yahoo.com/?p=us">Yahoo Search</a></li>
     </ul>-->
    </div>
    <div class="Menu top">
    <h3>Searching Engine Menu</h3>
    <div>
    Developers of this searching engine:
    <br>CHEN Shaoyu
    <br>CHEN Taiyou
    <br>SHANG Hang
    <br>ZHU Xinyu
    </div>
     <div>
    This searching engine is a project of the course COMP 4321 as HKUST
    </div>
    </div>

     <div class="sign top">
    <h3>Sign In</h3>
    <div>
    We used JQuery in our font page to make it pretty
    </div>
   
    </div>

</header>
<script>
function addTosearch(word){
  document.getElementById("input").value+=(" "+word);
}
function sentFeedBack(website){
  /*var url="http://10.89.116.121:8080/feedback.php";
    $.ajax({
    url: "http://10.89.116.121:8080/feedback.php",
    data: {query:document.getElementById("input").value,web:website},
    dataType: 'jsonp',
    type: 'GET',
    crossDomain: true
   
   
   
});*/
}
$("#help").click(function(){
  $(".Menu").slideUp(1000);
  $(".Tools").slideUp(1000);
  $(".Help").slideToggle(1000);
  $(".sign").slideUp(1000);
});
$("#tool").click(function(){
  $(".Menu").slideUp(1000);
  $(".Tools").slideToggle(1000);
  $(".Help").slideUp(1000);
  $(".sign").slideUp(1000);
});
$("#menu").click(function(){
  $(".Menu").slideToggle(1000);
  $(".Tools").slideUp(1000);
  $(".Help").slideUp(1000);
  $(".sign").slideUp(1000);
});
$("#sign").click(function(){
  $(".Menu").slideUp(1000);
  $(".Tools").slideUp(1000);
  $(".Help").slideUp(1000);
  $(".sign").slideToggle(1000);
});
</script>
<hr class="break_line" />

</header>

<main>


<%
double pangrankWeight=0.3;
double titleWeight=3;
double phraseWeight=20;
if(request.getParameter("similar")==null){// if it is regular search (not similar search)
  if (request.getParameter("search") != null) {// Get search parameter
    String que=request.getParameter("search"); 
    que=que.toLowerCase();//To lower case
    String myque=que.replaceAll("[^0-9a-zA-Z\"]"," ");//For Phrase
    String queWord=myque.replaceAll("\""," ");//For word
    HashMap<Vector<Integer>,Integer> phraseHashMap=Tools.getPhraseVector(myque);
    HashMap<Integer,Integer> wordHashMap=Tools.getWordVector(queWord);


    //Remove word in pharse from query
    //Personally I don't think it is a good idea to do that
    /*
    try{
      Set<Vector<Integer>> temp = phraseHashMap.keySet();
      for(Vector<Integer> a:temp){
        for(int i=0;i<a.size();i++){
          wordHashMap.remove(a.get(i));
        }
      }
    }catch(Exception e){}*/
//////////////////
    
    out.println("<div class=\"preprocess\">Phrase you entered: "+phraseHashMap);
    out.println("&nbsp;&nbsp;&nbsp;Words you entered: "+wordHashMap+"</div>");
    long time=System.currentTimeMillis();//Record start time


    TreeMap<Double,Integer> result=VectorSpace.getTop50Result(phraseHashMap,wordHashMap,pangrankWeight, titleWeight,phraseWeight);

    //Get the result
   
     
  
      //out.println("<div class=\"error\">Sorry No Result</div>");
     


   // if(result==null) return;
  
    long time2=System.currentTimeMillis();
    double totalTime=(time2-time)/1000.0;
    out.println("<div class=\"searchTime\">Total Result:"+result.size()+" ("+totalTime+" sceonds)</div>");
  
    que=que.replaceAll("[^0-9a-zA-Z]"," ");
    StringTokenizer st=new StringTokenizer(que);
    Vector<String> query=new Vector<String>();
  
    while(st.hasMoreTokens()){
      String word=st.nextToken();
      query.add(word);// Query words ready
    }
  
    boolean showDetail=(request.getParameter("detail")!=null);
    
    int totalResult=0;
    Iterator<Double> it = result.descendingKeySet().iterator();
  
    while(it.hasNext()){
      double score=it.next();
      try {
        PageFile p=PageInfo.getPage(result.get(score));
        if(p!=null){
          out.println(Tools.ExtractInfo(p,score,showDetail,query).replace("\n","<br>"));
          totalResult++;
        }
      } catch (IOException e) {}
    }
  }
}else{
  //Search similar
  HashMap<Integer,Integer> wordHashMap=Tools.getHashQuery(Integer.parseInt(request.getParameter("similar")));
  TreeMap<Double,Integer> result=VectorSpace.getTop50Result(null,wordHashMap,pangrankWeight, titleWeight,phraseWeight);
  if(result==null){
    out.println("<div class=\"error\">Sorry No Result</div>");
    return;
  }
  Vector<String> query=new Vector<String>();
  Iterator<Integer> itt =wordHashMap.keySet().iterator();
  while(itt.hasNext()){
    query.add(WordLib.getWord(itt.next()));
  }
  boolean showDetail=(request.getParameter("detail")!=null);
  long time=System.currentTimeMillis();//Record start time
  int totalResult=0;
  Iterator<Double> it = result.descendingKeySet().iterator();
  while(it.hasNext()){
    double score=it.next();
      try {
        PageFile p=PageInfo.getPage(result.get(score));
        if(p!=null){
          out.println(Tools.ExtractInfo(p,score,showDetail,query).replace("\n","<br>"));
          totalResult++;
        }
      } catch (IOException e) {}
  
  }     
  long time2=System.currentTimeMillis();
  double totalTime=(time2-time)/1000.0;
  
  out.println("<div class=\"searchTime\">Total Result:"+totalResult+" ("+totalTime+" sceonds)</div>");
}
%>
<div>

</div>
</main>
<hr/>
<div class="end">

<img src="img/search.png" width="200px"/>
Thank you for using our searching engine

</div>
</body>
</html>
