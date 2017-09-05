 app.directive('demoFileModel', function ($parse) {
    return {
        restrict: 'A', //the directive can be used as an attribute only
 
            /*
         link is a function that defines functionality of directive
         scope: scope associated with the element
         element: element on which this directive used
         attrs: key value pair of element attributes
         */
        link: function (scope, element, attrs) {
            var model = $parse(attrs.demoFileModel),
                modelSetter = model.assign; //define a setter for demoFileModel
 
                //Bind change event on the element
            element.bind('change', function () {
                //Call apply on scope, it checks for value changes and reflect them on UI
                scope.$apply(function () {
                    //set the model value
                    modelSetter(scope, element[0].files[0]);
                });
            });
        }
    };
});

app.controller('homeCtrl', ['$scope','$rootScope',  function($scope, $rootScope) {
	  $scope.modalShown = false;
	  $scope.showSection = false;
	  
	  $scope.toggleModal = function() {
	    $scope.modalShown = !$scope.modalShown;	    
	  };
	  
	  $scope.showSectionFn = function() {
		    $scope.showSection = true;	    
		  };
	  
	  $scope.showS3Fn = function() {
		  $rootScope.$emit('s3Emit', true);
	  };
	  
	  $scope.showSQSFn = function() {
		  $rootScope.$emit('sqsEmit', true);
	  };
	  
	  $scope.showSNSFn = function() {
		  $rootScope.$emit('snsEmit', true);
	  };
	 
}]);

app.controller('s3Ctrl', [ '$scope', '$http', '$rootScope',
		function($scope, $http, $rootScope) {
	

			$scope.s3DefaultImageSrc = '/images/s3_image.png';
			$scope.showS3 = false;
			$scope.s3Msg = "";
			 
			 $rootScope.$on('s3Emit', function(event, data) {
				 $scope.showS3 = data;	
			});
			 
			$scope.uploadFile = function() {

				var file = $scope.myFile;

				console.log('file is ');
				console.dir(file);

				//var uploadUrl = "/uploadAndRetrieveFromLocalMachine";
				var uploadUrl = "/uploadToS3";
				var fd = new FormData();
				var resp = null;
				//below is the file name that gets pushed to controller
				fd.append(file.name, file);
				
				$http.post(uploadUrl, fd, {
					transformRequest : angular.identity,
					transformResponse: angular.identity,
					headers : {
						'Content-Type' : undefined
					}
				}).success(function(response) {	
					$scope.s3Msg = "successfully uploaded to S3";					
					$scope.imageSrc = response;
					alert("successfully uploaded to S3");					
				}).error(function() {				
					alert("something went wrong");
				});
			};
}]);


app.controller('sqsCtrl', [ '$scope', '$http', '$rootScope',
                   		function($scope, $http, $rootScope) {
	
	$scope.sqsDefaultImageSrc = '/images/sqs_image1.jpg';
	$scope.showSQS = false;
	
	 $rootScope.$on('sqsEmit', function(event, data) {
		 $scope.showSQS = data;	
	});
	 
	 $scope.sendToSQS = function() {

			var sqsMessage = $scope.sqsMessage;

			console.log('message is '+sqsMessage);
			var uploadUrl = "/sendToSQS";
			
			var formData = {
					
					"subject" : $scope.sqsMessage,
					"message" : $scope.sqsMessage	
			};
			
			$http.post(uploadUrl, formData).success(function(response) {					
				alert("successfully sent to SQS");					
			}).error(function() {				
				alert("something went wrong");
			});
			
			

		};
}]);

app.controller('snsCtrl', [ '$scope', '$http', '$rootScope',
                       		function($scope, $http, $rootScope) {
    	
    	$scope.snsDefaultImageSrc = '/images/sns_image.png';
    	$scope.showSNS = false;
    	
    	 $rootScope.$on('snsEmit', function(event, data) {
    		 $scope.showSNS = data;	
    	});
    	 
    	 $scope.sendToSNS = function() {

    			var snsMessage = $scope.snsMessage;

    			console.log('message is '+snsMessage);
    			var uploadUrl = "/sendToSNS";
    			
    			var formData = {
    					
    					"subject" : $scope.snsMessage,
    					"message" : $scope.snsMessage	
    			};
    			
    			$http.post(uploadUrl, formData).success(function(response) {					
    				alert("successfully sent to SNS");					
    			}).error(function() {				
    				alert("something went wrong");
    			});
    			
    			

    		};
    }]);

app.directive('modalDialog', function() {
	  return {
	    restrict: 'E',
	    scope: {
	      show: '='
	    },
	    replace: true, // Replace with the template below
	    transclude: true, // we want to insert custom content inside the directive
	    link: function(scope, element, attrs) {
	      scope.dialogStyle = {};
	      if (attrs.width)
	        scope.dialogStyle.width = attrs.width;
	      if (attrs.height)
	        scope.dialogStyle.height = attrs.height;
	      scope.hideModal = function() {
	        scope.show = false;
	      };
	    },
	    template: "<div class='ng-modal' ng-show='show'><div class='ng-modal-overlay' ng-click='hideModal()'></div><div class='ng-modal-dialog' ng-style='dialogStyle'><div class='ng-modal-close' ng-click='hideModal()'>X</div><div class='ng-modal-dialog-content' ng-transclude></div></div></div>"
	  };
	});
