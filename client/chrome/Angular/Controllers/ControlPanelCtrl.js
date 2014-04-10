function ControlPanelCtrl($scope) {

    $scope.automationMode = 'Forward';
    $scope.world = 'Empty Room';

    $scope.setAutomationMode = function(mode) 
    {
        $scope.automationMode = mode;
        switch (mode) {
            case 'Forward':
              cntr.setAutomationMode(1);
              break;
            case 'Party Mode':
              cntr.setAutomationMode(2);
              break;
        }
    };

    $scope.setWorld = function(world) 
    {
        $scope.world = world;
    };

}