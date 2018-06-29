'use strict';

angular.module('myApp.view1', ['ngRoute'])

.config(['$routeProvider', function($routeProvider) {
  $routeProvider.when('/view1', {
    templateUrl: 'view1/view1.html',
    controller: 'View1Ctrl'
  });
}])

.controller('View1Ctrl', ['$rootScope', '$window','$http', '$scope', function($rootScope, $window, $http, $scope) {

    let user = $rootScope.user;
    let rootApi = 'https://1-dot-lamareddit-205114.appspot.com/_ah/api/messageendpoint/v1/';
    getBestMessage();

    $scope.addMessage = function() {
        let url = "entity/"+$rootScope.user.author_name+"/"+$rootScope.user.id+"/"+encodeURI($scope.stitle)+"/"+encodeURI($scope.smessage)+"/"+encodeURI($scope.sURL);
        $http.post(rootApi+url).then(function(response){
            getAllMessage();
            console.log("response");
        });
    };

    $scope.vote = function(message, type){
        let url = "likeMessage?idMessage="+encodeURI(message.key.name)+"&typeLike="+encodeURI(type)+"&userID="+encodeURI($rootScope.user.id);
        $http.post(rootApi+url).then(function(response){
            console.log(response);
            if(response.data == ""){
                console.log("erreur");
            } else {
                getAllMessage();
            }
        });
    };

    function getBestMessage() {
        let messages;
        $http.get(rootApi+'getBestMessage').then(function(response){
            messages = response.items;
            $scope.messages = response.data.items;
            console.log(response.data.items);
        });
        return messages;
    }

    // $window.init = function() {
    //     console.log("windowinit called");
    //
    //     gapi.client.load('messageendpoint', 'v1', function() {
    //         console.log("message api loaded");
    //         $scope.listMessage();
    //         console.log($scope.messages)
    //     }, rootApi);
    // }

}]);