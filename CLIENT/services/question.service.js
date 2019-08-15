app.service('$QuestionService', ['$http', 'ApiPath','$TeamService',"$rootScope", function ($http, ApiPath, $TeamService,$rootScope) {
  //Mantem dados do usuario autenticado
  this.question = {}
    
  this.setActiveQuestion = function(question){
      this.question = question;
  }

  this.getActiveQuestion = function(question){
     return this.question;
  }
  
    this.markAlternative = function(alternative){
        $rootScope.connectionState = "Por favor aguarde...";
        $("#modalLoading").modal();
        var config = {
            headers: {
                Authorization: localStorage.getItem("cleangameToken")
            }
        }
        return $http.post(ApiPath + '/rooms/markalternative',alternative, config).then(function(response) {
            $("#modalLoading").modal('hide');      
             console.log("Question")                     
                                  
            return response
        }).catch(function (err) {
            console.log("ERRO: Falha ao marcar alternativa...")
            return err;
        });
    }




    this.getTip = function(question_id){
        var config = {
            headers: {
                Authorization: localStorage.getItem("cleangameToken")
            }
        }
        return $http.get(ApiPath + '/question/'+question_id+'/'+$TeamService.getActiveTeam().id+'/tip', config).then(function (response) {
            return response;                         
        }).catch(function (err) {
            console.log("Falha ao consultar dica...")
            return err;
        });
    }


    this.getTip1 = function(question_id){
        var config = {
            headers: {
                Authorization: localStorage.getItem("cleangameToken")
            }
        }
        return $http.get(ApiPath + '/question/'+question_id+'/'+$TeamService.getActiveTeam().id+'/tip1', config).then(function (response) {
            return response;                         
        }).catch(function (err) {
            console.log("Falha ao consultar dica...")
            return err;
        });
    }

    this.getTip2 = function(question_id){
        var config = {
            headers: {
                Authorization: localStorage.getItem("cleangameToken")
            }
        }
        return $http.get(ApiPath + '/question/'+question_id+'/'+$TeamService.getActiveTeam().id+'/tip2', config).then(function (response) {
            return response;                         
        }).catch(function (err) {
            console.log("Falha ao consultar dica...")
            return err;
        });
    }

    this.getTip3 = function(question_id){
        var config = {
            headers: {
                Authorization: localStorage.getItem("cleangameToken")
            }
        }
        return $http.get(ApiPath + '/question/'+question_id+'/'+$TeamService.getActiveTeam().id+'/tip3', config).then(function (response) {
            return response;                         
        }).catch(function (err) {
            console.log("Falha ao consultar dica...")
            return err;
        });
    }

    this.skip = function(question_id){
        $rootScope.connectionState = "Por favor aguarde...";
        $("#modalLoading").modal();
        var config = {
            headers: {
                Authorization: localStorage.getItem("cleangameToken")
            }
        }
        return $http.get(ApiPath + '/question/'+question_id+'/'+$TeamService.getActiveTeam().id+'/skip', config).then(function (response) {
            $("#modalLoading").modal('hide');
            return response;                         
        }).catch(function (err) {
            console.log("Falha ao consultar dica...")
            return err;
        });
    }


    this.addBadsmell = function(dto){
        $rootScope.connectionState = "Por favor aguarde...";
        $("#modalLoading").modal();
        var config = {
            headers: {
                Authorization: localStorage.getItem("cleangameToken")
            }
        }

        return $http.post(ApiPath + '/question/add_badsmell/', dto, config).then(function (response) {
            $("#modalLoading").modal('hide');
            return response;                         
        }).catch(function (err) {
            console.log("Falha ao consultar dica...")
            return err;
        });
    }


    this.update = function(){
        $rootScope.connectionState = "Por favor aguarde...";
        $("#modalLoading").modal();
        var config = {
            headers: {
                Authorization: localStorage.getItem("cleangameToken")
            }
        }

        return $http.post(ApiPath + '/question/update/', this.getActiveQuestion(), config).then(function (response) {
            $("#modalLoading").modal('hide');
            return response;                         
        }).catch(function (err) {
            console.log("Falha ao consultar dica...")
            return err;
        });
    }

   

    this.getBadsmell = function(dto){
        $rootScope.connectionState = "Por favor aguarde...";
        $("#modalLoading").modal();
        var config = {
            headers: {
                Authorization: localStorage.getItem("cleangameToken")
            }
        }

        return $http.get(ApiPath + '/question/badsmell/'+this.getActiveQuestion().id, config).then(function (response) {
            $("#modalLoading").modal('hide');
            return response;                         
        }).catch(function (err) {
            console.log("Falha ao consultar dica...")
            return err;
        });
    }

   
}])