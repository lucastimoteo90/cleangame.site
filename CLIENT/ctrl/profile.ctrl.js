app.controller('ProfileCtrl', function ($rootScope, $location, $scope) {
   
  console.log("Controlador ProfileCtrl carregado!");
   
    console.log("User", $rootScope.user)
    $rootScope.sendMsg = "";
    //Carregamento padrão
    //$rootScope.loadTemplate('./views/productsList.template.html');
  
  
    //renewNavs();
    // $rootScope.activetab = $location.path();
  });
  
  