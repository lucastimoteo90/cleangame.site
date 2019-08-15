app.controller('SkeletonCtrl', function ($rootScope,$routeParams, $location, $scope, $UserService,$RoomService, $TeamService) {
  console.log("Controlador SkeletonCtrl carregado!");
  $rootScope.connectionState = "Por favor aguarde...";
  $("#modalLoading").modal();

  $rootScope.user = {};

  $rootScope.user.isLoged = false;
  $rootScope.msg = {};
  
  /*Funções de uso geral do sistema*/
  function getUser() {    
    $UserService.getUserData().then(function (response) {
      /**Verifica se usuario esta logado */
      if (response.status == 200) {
        $rootScope.user = response.data;
        $rootScope.user.isLoged = true;

        $rootScope.loadMainContent('rooms');
        $("#modalLoading").modal('hide');
      } else if (response.status == 403) {
        $rootScope.loadMainContent('home')
        $("#modalLoading").modal('hide');
      }else{
        rootScope.connectionState = "Sem conexão com api, nova tentativa em execução";
        getUser();
        //setTimeout(getUser(), 5000)
        //alert("Sem conexão com API")
      }
    })
  }

  
  $rootScope.openLoginModal = function(){
    $("#modalLogin").modal();
  }
  $rootScope.openLoginToggle = function(){
    $('#dropdown-toggle-login').dropdown('toggle');
  }
  
  $rootScope.newUser = function(){
    $("#modalLogin").modal('hide');
    $("#modalNewUser").modal('show');
  }

  $rootScope.linkshared = "link"
  $rootScope.newSharedRoom = function(){
    $("#modalSharedRoom").modal('show');
  }

  

  $rootScope.loadMainContent = function (template) {
    $location.state('#');//Não permite sair da pagina diretamente
    $rootScope.divmaincontent = './views/' + template + '.template.html';
  }

  
    
  $rootScope.logIn = function (login) {
      $("#modalLogin").modal('hide');
      $UserService.login(login).then(function (data) {
        $UserService.getUserData().then(function (response) {
          if (response.status == 200) {
             $("#modalLogin").modal('hide'); 
             getUser();
             $('.dropdown.open').removeClass('open');             
          }else{
            $rootScope.user.isLoged = false;
            $rootScope.msg.errorLogin = "Falha no login, tente novamente!";
            $rootScope.openLoginToggle()
          }
        })
      })
    }
  
    $rootScope.logOut = function (login) {
      $UserService.logout();
      location.reload();
    }
  
  
  
    
    
    
    getUser();
  //SUPER GAMBIARRA  
  //Se existir convite  
  if(typeof $routeParams.inviteid !== "undefined"){     
       if(localStorage.getItem("cleangameToken") ){
          console.log("Usuário Logado: ");
          $TeamService.getRoom($routeParams.inviteid).then(function (room) {
              console.log("connectionStateRREGANDO DADADOS SALA REFERENTE AO CONVITE", room)

              //Adicionar uconnectionStateário ao team 

              //Carrega a sconnectionStatea do convite...(multiplayer)
              $RoomService.setActiveRoom(room);
              team={id:$routeParams.inviteid}
              $TeamService.setActiveTeam(team);
              
              $rootScope.invited = true;
              $rootScope.loadMainContent("rooms");
          })          /*
          * Consulta a sala referente ao convite
          * O id do convite é o id do team
          */



          //Direciona para a sala correta
    

       }else{
          console.log("Usuário não Logado: ");
          $rootScope.loadMainContent('new-user');

       }

      //CARREGAMENTO PADRÃO;  
  }else{

  /**Uso jquery compatibilidade com bootstrap */
  //$rootScope.loadMainContent('home');

  //Carregamento padrão
  //$rootScope.loadTemplate('./views/productsList.template.html');

  //renewNavs();
  // $rootScope.activetab = $location.path();
  }//FECHA CARRAGAMENTO PADRAO

  if(typeof $routeParams.battleid !== "undefined"){ 
    console.log($routeParams.battleid);
    $rootScope.battle = true;
    $rootScope.battleid = $routeParams.battleid;
    
    $RoomService.getRoom($routeParams.roomid).then(function(response){
      $RoomService.setActiveRoom(response.data);
      $RoomService.createTeam().then(function(response){
        console.log("CREATE TEAM")
        $TeamService.setActiveTeam(response.data);
      })
    })
    //CRIA TEAM 
    


    //Cria novo team 
    //$TeamService.ne


    //Carrega sala
    $rootScope.loadMainContent("rooms");

   }else{
      console.log("Usuário não Logado: ");
      $rootScope.loadMainContent('new-user');

   }
  

  




});


