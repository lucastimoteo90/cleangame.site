app.controller('ReportCtrl', function ($rootScope,Domain, $location, $interval, $scope,$RoomService, $MediumRoomService, $QuestionService,$SocketService, $TeamService) {
  console.log("Controlador REPORT Ctrl carregado!");

  label = {};
  label.newEasyRoomTitle = "New medium room";
  label.nameRoom = "Room name";
  label.descriptionRoom = "Room description";
  label.isPublicRoom = "Is public room?";
  label.btnCreateRoom = "Create!";
  label.git = "Git clone:"

  $scope.loadingtip = false;

  $scope.leader = {};

  $scope.label = label;

  $scope.step1 = true;
  $scope.step2 = false;

  $scope.tip1Solicitada = false;
  $scope.tip2Solicitada = false;
  $scope.tip3Solicitada = false;

  $scope.question = {};
  $scope.questions = {};

  $scope.resume = {};
  $scope.resume.hits = 0;
  $scope.position = 0;

  $scope.panel = {}
  $scope.panel.time = 0;
  
  $scope.rankingStyle = {"font-weight": "bold"}
  $scope.reports = {}
  $RoomService.getReport().then(function(response){
    $scope.reports = response.data;
  })

 
});

