app.controller('RoomsCtrl', function ($rootScope, $location, $scope, $RoomService, $EasyRoomService,$TeamService) {
  console.log("Controlador RoomsCtrl carregado!");
  $rootScope.pagesHistory.push("rooms")
  label = {};
  label.name = "Nome";
  label.cancel = "Cancelar";
  label.description = "Descrição";
  label.descriptionHelp = "Faça uma pequena descrição da sala...";
  label.gitUrl = "Informe um repositório github";
  label.roomPublic = "A sala é publica?(Permite a participação de qualquer usuário!)";
  label.newRoom = "Nova sala";
  label.saveNewRoom = "Cadastrar";

  $scope.label = label;
  
  $rootScope.exibeChat = false;

  $scope.roomsAdministrator = {};
  $scope.roomsMember = {}

  //Verifica se existe convite 
  if($rootScope.invited){
    $rootScope.loadMainContent("rooms/"+$RoomService.getActiveRoom().type.toLowerCase()+'/room');
  }

  //Verifica se existe batalha 
  if($rootScope.battleid){
    $rootScope.loadMainContent("rooms/hard/room");
  }

  function getUserRoomsAdmin() {
    $RoomService.getUserRoomsAdmin().then(function (response) {
      $scope.roomsAdministrator = response.data;
    })
  }

  function getUserRoomsMember() {
    $RoomService.getUserRoomsMember().then(function (response) {
      $scope.roomsMember = response.data;
      if($scope.roomsMember.length < 1 ){
        $scope.msgNoRoomsMember = "Você não esta inscrito em nenhuma sala ate o momento...";
      }      
    })
  }

  function findRooms(keyword){
    $RoomService.findRooms(keyword).then(function(response){
       if(response.data.length == 0){
         $scope.msgFind = "Nenhma sala encontrada para consulta realizada!"
       }else{
         $scope.roomsFindList = response.data
       }
    })
  }
  
  function roomSubscribe(room){
    $RoomService.roomSubscribe(room).then(function(response){
      if(response.data.length == 201){
      
      }
   })
  }
  



  getUserRoomsAdmin();
  getUserRoomsMember()

  $scope.openViewNewRoom = function () {
    $("#modalNewRoom").modal('show');
  }


  $scope.open = function(room){
    $RoomService.open(room).then(function(response){
      console.log(response);
      getUserRoomsAdmin();
      getUserRoomsMember()
    })
  }

  $scope.close = function(room){
    $RoomService.close(room).then(function(response){
      getUserRoomsAdmin();
      getUserRoomsMember()
      console.log(response);
    })
  }

  $scope.report = function(room){
    $RoomService.setActiveRoom(room);
    $rootScope.loadMainContent('/rooms/report/report');
  }

  $scope.reset = function(room){

  }

  $scope.createNewEasyRoom = function(){
    $rootScope.createRoom = true;
    $("#modalNewRoom").modal('hide');
    $rootScope.loadMainContent('rooms/easy/create');
  }

  $scope.createMediumRoom = function(){
    $rootScope.createRoom = true;
    $("#modalNewRoom").modal('hide');    
    $rootScope.loadMainContent('rooms/medium/create');
  }

  $scope.createHardRoom = function(){
    $rootScope.createRoom = true;
    $("#modalNewRoom").modal('hide');    
    $rootScope.loadMainContent('rooms/hard/create');
  }


  $scope.insertNewRoom = function (room) {
    /*
    $RoomService.insertNewRoom(room).then(function (response) {
      if (response.status == 201) {
        $("#modalNewRoom").modal('hide');
        getUserRoomsAdmin();
      }
    })
    */

    $EasyRoomService.insertNewRoom(room).then(function (response) {
      if (response.status == 201) {
        $("#modalNewRoom").modal('hide');
        getUserRoomsAdmin();
      }
    })

  }

  $scope.deleteRoom = function(room){
    $RoomService.delete(room).then(function (response) {
      getUserRoomsAdmin();
    })
  }


  $scope.accessRoom = function(room){
    $RoomService.setActiveRoom(room);
    $("#modalLoading").modal(); 
    //Criar uma seção ou team
    $RoomService.createTeam().then(function(response){
      console.log("CREATE TEAM")
      $RoomService.setActiveRoom(room);
      $TeamService.setActiveTeam(response.data);
      $("#modalLoading").modal('hide');
      if(room.type == "EASY"){
        $rootScope.loadMainContent('rooms/easy/room');
      }if(room.type == "MEDIUM"){
        $rootScope.loadMainContent('rooms/medium/room')
      }else{
        $rootScope.loadMainContent('rooms/hard/room');
      }
    })

     


   

  }
  
  $scope.editRoom = function(room){
    $RoomService.setActiveRoom(room);
    if(room.type == 'EASY'){
      $rootScope.loadMainContent('rooms/easy/create');
    }
    
  }

  $scope.nav = {};
  $scope.openTabMember = function(){
    $scope.nav.admin = false;
    $scope.nav.member = true;
  }

  $scope.openTabAdmin = function(){
    $scope.nav.admin = true;
    $scope.nav.member = false;
  }
  
  $scope.openTabAdmin();

  $scope.findRooms = function(keyword){
    if(keyword.length < 4){
      $scope.roomsFindList = [];
    }else{
      findRooms(keyword)
    }
    
  }

  $scope.roomSubscribe = function(room){
    console.log(room)
    roomSubscribe(room);
  }

  //Carregamento padrão
  //$rootScope.loadTemplate('./views/productsList.template.html');


  //renewNavs();
  // $rootScope.activetab = $location.path();
});

