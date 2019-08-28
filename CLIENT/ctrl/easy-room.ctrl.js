app.controller('EasyRoomCtrl', function ($rootScope,Domain,$sce, $location, $scope,$RoomService, $EasyRoomService, $QuestionService, $SocketService, $TeamService) {
  console.log("Controlador EasyRoomCtrl carregado!");
  



  label = {};
  label.newEasyRoomTitle = "New easy room";
  label.nameRoom = "Room name";
  label.descriptionRoom = "Room description";
  label.isPublicRoom = "Is public room?";
  label.btnCreateRoom = "Create!";
  label.newQuestion = "New Question"
  label.correctAlternative = "Correct alternative";
  label.fakeAlternative = "Fake alternative";
  label.btnInsertQuestion = "Insert question!"

  $scope.leader = {};

  $scope.label = label;

  $scope.step1 = true;
  $scope.step2 = false;

  $scope.question = {};
  $scope.questions = {};

  $scope.sendMsg = "";
  $scope.consoleChat = '...';

  $scope.resume = {};
  $scope.resume.hits = 0;

  console.log("DADOS DA SALA", $RoomService.getActiveRoom());

  //Corrigir
  $scope.room = $RoomService.getActiveRoom();
  
  
  

  //Função ativa modo multiplayer na sala
  function multiplayer(){
    //Funções socketio
    //conecta usuário servidor
  $SocketService.conect();

  //Registra usuário na sala
  $SocketService.registerUser($rootScope.user);
  $SocketService.registerUserRoom($TeamService.getActiveTeam().id);
 
  
  


  //Inicia captura da posição do mouse e envia servidor socket
  window.addEventListener('mousemove', function(e) {
    var mouse = {
        page: {
            x: e.pageX,
            y: e.pageY
        },
        client: {
            x: e.clientX,
            y: e.clientY
        }
    };

    $SocketService.moveMouse(mouse);

    //console.log("POSICAO MOUSE",);
    //var screenWidth = screen.width;
    //var screenHeight = screen.height;    
  })


  usuarios = [];
  count_user_remot = 0;
  //Obtem posição mouse demais participantes da sala.
  
  $SocketService.socket.on("movereceiver", function(moviment) {    	
    //msgobj = JSON.parse(msg);
    //console.log("RECEBENDO MOVIMENTAÇÂO", moviment); 
    
    if(usuarios.indexOf(moviment.user) > -1){

    }else{
      if(moviment.user != $rootScope.user.mail){
        count_user_remot++;
        $("#userremoto"+count_user_remot).html("."+moviment.user);
        $("#userremoto"+count_user_remot).show()
        usuarios[count_user_remot++] = moviment.user
       // console.log("NOVO USUARIO NA SALA",moviment.user);
       }
    }
    
    if(moviment.user != $rootScope.user.mail)
      $("#userremoto"+usuarios.indexOf(moviment.user)).css({top: (($(window).height()/300)*moviment.y), left: (($(window).width()/300)*moviment.x), position:'absolute'});
  

    });

  
  //array usuários logados na sala
 
  $SocketService.socket.on("sendchatmsg", function(mensagem){
    console.log("RECEBEU MSG")
    $scope.consoleChat = $sce.trustAsHtml($scope.consoleChat+"<br>");
    $scope.consoleChat = $sce.trustAsHtml($scope.consoleChat +"<b>"+ mensagem.usermail+":</b>");
    $scope.consoleChat = $sce.trustAsHtml($scope.consoleChat + mensagem.text);
    //$sce.trustAsHtml($scope.html)
  })

  $SocketService.socket.on("new_user_in_room", function(usermail) {    	
   
    if(usermail != $rootScope.user.mail){
     count_user_remot++;
     $("#userremoto"+count_user_remot).html("."+usermail);
     $("#userremoto"+count_user_remot).show()
     usuarios[count_user_remot++] = usermail
     console.log("NOVO USUARIO NA SALA",usermail);
    }
   
  });

  /**RECEBE NOTIFICAÇÂO DE ALTERNATIVA */
  $SocketService.socket.on("makealternative", function(msg) { 
    console.log("alternativ receiver",msg)
    if(md5($scope.question.alternatives[0]) == msg.data.md5correct){
      $rootScope.answerCorrect = "A "+$scope.question.alternatives[0];
    }else if(md5($scope.question.alternatives[1]) == msg.data.md5correct){
      $rootScope.answerCorrect = "B "+$scope.question.alternatives[1];          
    }else if(md5($scope.question.alternatives[2]) == msg.data.md5correct){
      $rootScope.answerCorrect = "C "+$scope.question.alternatives[2];          
    }else{
      $rootScope.answerCorrect = "D "+$scope.question.alternatives[3];          
    } 
    
    showScoreAlternative(msg.data)
    loadQuestion();    
   
    
  })  	

  

  $SocketService.socket.on("skipalternative", function(msg) { 
    console.log("RECEBENDO SKIP!!", msg)  
    loadQuestion(); 
    //$scope.tip = msg.tip;    
  })  	

  $SocketService.socket.on("gettip", function(msg) { 
    console.log("RECEBENDO DICA!!", msg)    
    $scope.tip = msg.tip;    
  })  	



}





  $scope.panel = {}
  $scope.panel.time = 0;
  

  function loadResume(){
    $("#modalLoading").modal("hide");
    $RoomService.getResume().then(function(response){
      $scope.resume = response.data;
      $scope.progress = Math.round(((response.data.hits + response.data.errors + response.data.skips)*100 / response.data.totalQuestions))
      
      $scope.progressStyle = {'width':$scope.progress+'%'} 
    })
    loadRanking();
  }

  function loadRanking(){
    $RoomService.getRanking().then(function(response){
      $scope.ranking = response.data.usersRankingDTO;

      //Gambiarra para não matar server
      $scope.ranking.forEach(ranking => {
        if($rootScope.user.mail == ranking.email){
           $scope.position = ranking.position;     
           console.log("AQUI", $scope.resume.position)     
        }
      });
      //alert($scope.progress)
    })
  }

  function loadQuestion(){  
    $scope.tip = null;
    $("#modalLoading").modal()
    loadResume();
    $("#modalLoading").modal()
    $scope.panel.time = 0;  
    $EasyRoomService.getQuestion().then(function(response){
       if(response.data.id != null){
         $scope.question = response.data;
         $("#modalLoading").modal('hide');         
       }else{
        $("#modalLoading").modal('hide');
        $("#modalMaisPontos").modal('hide');
        $("#modalMenosPontos").modal('hide');
        setInterval(function(){
          $scope.$apply(function () {
            loadRanking();
         })
      },5000) 
        $rootScope.loadMainContent('rooms/easy/congratulations')
        
          
         

     }       
       console.log($scope.question)
    })
  }

  function loadQuestionSocket(data = null){            
      
    loadResume();
    loadRanking()
    $scope.tip = null;
    $scope.panel.time = 0;  
    $EasyRoomService.getQuestion().then(function(response){
       if(response.data.id != null){
         $scope.question = response.data;         
       }else{
         $rootScope.loadMainContent('rooms/easy/congratulations')
       }       
       $SocketService.makeAlternative(data);
       console.log($scope.question)
    })
  }

  

  function moveStep1(){
    $scope.step2 = false;
    $scope.step1 = true;
    getQuestions();
  }

  function moveStep2(){
    $scope.step2 = true;
    $scope.step1 = false;
    getQuestions();
  }
 

  function getQuestions(){
    $EasyRoomService.getQuestions().then(function(response){
      $scope.questions = response.data;
    })
  }


  $scope.getTip = function(){
    $QuestionService.getTip($scope.question.id).then(function(response){
      $scope.tip = response.data.tip
      $SocketService.getTip(response.data.tip)
    })
  }

  $scope.getTip1 = function(){
    $QuestionService.getTip1($scope.question.id).then(function(response){
      $scope.tip = response.data.tip
      $SocketService.getTip(response.data.tip)
    })
  }

  $scope.getTip2 = function(){
    $QuestionService.getTip2($scope.question.id).then(function(response){
      $scope.tip = response.data.tip
      $SocketService.getTip(response.data.tip)
    })
  }

  $scope.getTip3 = function(){
    $QuestionService.getTip3($scope.question.id).then(function(response){
      $scope.tip = response.data.tip
      $SocketService.getTip(response.data.tip)
    })
  }


  $scope.skip = function(){
    $QuestionService.skip($scope.question.id).then(function(response){      
      loadQuestion();
      $SocketService.skipAlternative(response.data)
    })
  }

  $scope.restart = function(){
    $RoomService.restart().then(function(response){
    room = $RoomService.getActiveRoom();  
   // $RoomService.createTeam().then(function(response){
      
        $RoomService.setActiveRoom(room);
        $TeamService.setActiveTeam(response.data);
  
        if(room.type == "EASY"){
          $rootScope.loadMainContent('rooms/easy/room');
        }else{
          $rootScope.loadMainContent('rooms/medium/medium-room');
        }
     // })
      //$rootScope.loadMainContent('rooms/easy/room')
    })
  }


  function showScoreAlternative(data){
    $("#modalLoading").modal("hide");
    alternative = data;
    $rootScope.score = data.score 
    
    if(data.correct){
      $("#modalMaisPontos").modal();
      $("#maisPontos").show();
      resto = 0;
      efeito = setInterval(function(){
        
         $scope.$apply(function () {
         if( (resto++ %2) == 0){
          $("#maisPontos").hide();
         }else{
          $("#maisPontos").show();
         }
        })
      },100)

      setTimeout(function(){
        $scope.$apply(function () {
          $("#maisPontos").hide();
          $("#modalMaisPontos").modal('hide');
          clearInterval(efeito)
      });        
      },5000);
      
       
    }else{
      $("#modalMenosPontos").modal();
      $("#menosPontos").show();
      resto = 0;
      efeito = setInterval(function(){
        
        $scope.$apply(function () {
        if( (resto++ %2) == 0){
          $("#menosPontos").hide();
        }else{
          $("#menosPontos").show();
        }
       })
     },100)
      setTimeout(function(){
        $scope.$apply(function () {
          $("#menosPontos").hide();
          $("#modalMenosPontos").modal('hide');
          clearInterval(efeito)
      });        
      },3000);
      
    }

   
     
  }


  $scope.markAlternative = function(option){
     alternative = {};
     alternative.question = $scope.question.id;
     alternative.answer = $scope.question.answer;     
     alternative.md5answer = md5($scope.question.alternatives[option]);

     $QuestionService.markAlternative(alternative).then(function(response){
         
        if(md5($scope.question.alternatives[0]) == response.data.md5correct){
          $rootScope.answerCorrect = "A "+$scope.question.alternatives[0];
        }else if(md5($scope.question.alternatives[1]) == response.data.md5correct){
          $rootScope.answerCorrect = "B "+$scope.question.alternatives[1];          
        }else if(md5($scope.question.alternatives[2]) == response.data.md5correct){
          $rootScope.answerCorrect = "C "+$scope.question.alternatives[2];          
        }else{
          $rootScope.answerCorrect = "D "+$scope.question.alternatives[3];          
        } 
          

         showScoreAlternative(response.data)

         loadQuestionSocket(response.data); 
         loadQuestion(); 
     })
  }

  $scope.moveQuestionsTab = function(){
    if($scope.room.id != null){
      moveStep2()
    }
  }

  $scope.moveRoomTab = function(){
    if($scope.room.id != null){
      moveStep1()
    }
  }


  $scope.createRoom = function(room){
    room.type = "EASY"
    $EasyRoomService.insertNewRoom(room).then(function(response){
      if(response.status==201){
        $EasyRoomService.setActiveRoom(response.data)
        moveStep2();
      }      
    })
  }

  $scope.insertQuestion = function(question){
    question.md5correct = md5(question.correct);
    $EasyRoomService.insertQuestion(question).then(function(response){
       $scope.question = angular.copy($scope.leader); 
       getQuestions()      
    })
  }

  $scope.editQuestion = function(question){
    $scope.question = question;
  }

  $scope.sharedLink = function(){
    $rootScope.linkshared = Domain + "/invite/"+$TeamService.getActiveTeam().id;
    $("#modalSharedRoom").modal('show');
  }

  $scope.sendChatMensage = function(msg){
    $SocketService.sendChatMessage(msg);
    
    $scope.consoleChat = $sce.trustAsHtml($scope.consoleChat+"<br>");
    $scope.consoleChat = $sce.trustAsHtml($scope.consoleChat +"<b>"+ $scope.user.mail+":</b>");
    $scope.consoleChat = $sce.trustAsHtml($scope.consoleChat + msg);
    $scope.sendMsg = "";
  }



  


  if( $rootScope.createRoom){
    
    $rootScope.createRoom = false;
  }else{
  
    $EasyRoomService.setActiveRoom($RoomService.getActiveRoom());
    
    
    setInterval(function(){
      $scope.$apply(function () {
        $scope.panel.time++;
        if($scope.panel.time == 40){
          $scope.getTip();
        }
        if($scope.panel.time == 80){
          $scope.getTip();
        }
  
  
    });
     
    },1000)  

    multiplayer();

    loadQuestion();
    loadResume();

    
  }
  

  //Carregamento padrão
  //$rootScope.loadTemplate('./views/productsList.template.html');


  //renewNavs();
  // $rootScope.activetab = $location.path();
});

