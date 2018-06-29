    //
    // 'use strict';
    //
    // angular
    //     .module('myApp.logger')
    //     .controller('logger', logger);
    //
    // function logger() {
    //
    //     console.log("tototot");
    //     function onSignIn(googleUser) {
    //         console.log("to");
    //         var profile = googleUser.getBasicProfile();
    //         console.log('ID: ' + profile.getId()); // Do not send to your backend! Use an ID token instead.
    //         console.log('Name: ' + profile.getName());
    //         console.log('Image URL: ' + profile.getImageUrl());
    //         console.log('Email: ' + profile.getEmail()); // This is null if the 'email' scope is not present.
    //     }
    //
    //
    // };

    'use strict';

    angular.module('myApp.logger', ['ngRoute'])

        .config(['$routeProvider', function($routeProvider) {
            $routeProvider.when('/', {
                templateUrl: 'index.html',
                controller: 'Logger'
            });
        }])

        .controller('Logger', [function() {


        }]);