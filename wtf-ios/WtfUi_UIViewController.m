#import "WtfTools.h"

#import "WtfUi_UIViewController.h"

@implementation WtfUi_UIViewController

-(instancetype) on:(NSString *)eventName :(WtfEventHandler) handler
{
    [self on:eventName :handler :nil];
    return self;
}

-(instancetype) on:(NSString *)eventName :(WtfEventHandler) handler :(JSO *)initData
{
    if(nil==handler){
        return self;
    }
    if(nil==self.eventMap){
        self.eventMap=[NSMutableDictionary dictionary];
    }
    if(nil==self.eventMap[eventName]){
        self.eventMap[eventName]=[NSMutableArray array];
    }
    [self.eventMap[eventName] addObject:handler];
    return self;
}

-(instancetype) trigger :(NSString *)eventName :(JSO *)triggerData
{
    NSLog(@"trigger(%@) is called.", eventName);
    NSArray * dict =self.eventMap[eventName];
    if(nil!=dict){
        NSUInteger c =[dict count];
        for(int i=0; i<c; i++){
            WtfEventHandler hdl=[dict objectAtIndex:i];
            if(nil!=hdl){
                if(nil==triggerData) triggerData=[JSO id2o:@{}];
                NSLog(@"with triggerData %@", [triggerData toString]);
                hdl(eventName, triggerData);
            }
        }
    }
    return self;
}

-(instancetype) off :(NSString *)eventName
{
    if(nil==self.eventMap){
        self.eventMap=[NSMutableDictionary dictionary];
    }
    self.eventMap[eventName]=[NSMutableArray array];
    return self;
}

-(instancetype) trigger :(NSString *)eventName
{
    return [self trigger:eventName :nil];
}

- (void)initUi
{
    [self resetTopBarBtn];
    
    NSString *title = [[self.uiData getChild:@"title"] toString];
    
    [self on:WtfEventBeforeDisplay :^(NSString *eventName, JSO *extraData) {
        
        [self resetTopBarStatus];
        if(nil!=title){
            [self setTopBarTitle:title];
        }
        [self setNeedsStatusBarAppearanceUpdate];
    } :nil];
}

-(BOOL) finishUi
    {
        BOOL flagIsLast=YES;
        id<UIApplicationDelegate> ddd = [UIApplication sharedApplication].delegate;
        UINavigationController *nnn=self.navigationController;
        if (nnn){
            NSArray *vvv = nnn.viewControllers;
            if(nil!=vvv){
                if(vvv.count>1){
                    [self.navigationController popViewControllerAnimated:YES];
                    flagIsLast=NO;
                }else{
                    //[self.navigationController popViewControllerAnimated:YES];
                    
                    ddd.window.rootViewController = nil;
                    [self dismissViewControllerAnimated:YES completion:^{
                        NSLog(@"Current View dismissViewControllerAnimated");
                    }];
                    [self trigger:WtfEventAfterClose :self.responseData];
                }
            }
            if(flagIsLast==YES){
                NSLog(@" flagIsLast==YES for navigationController");
            }
        }else{
            UIViewController *rootUi=ddd.window.rootViewController;
            if (rootUi == self){
                NSLog(@" flagIsLast==YES for rootViewController root = self");
                flagIsLast=YES;
            }
            [self dismissViewControllerAnimated:YES completion:^{
                NSLog(@"Current View dismissViewControllerAnimated");
            }];
        }
        return flagIsLast;
    }
    - (void) closeUi :(JSO*)resultJSO
    {
        [self setResponseData:resultJSO];
        [self closeUi];
//[self trigger:WtfEventWhenClose :self.responseData];
    }
- (void) closeUi
{
//    BOOL flagIsLast=YES;
//
//    id<UIApplicationDelegate> ddd = [UIApplication sharedApplication].delegate;
//    UINavigationController *nnn=self.navigationController;
//    if (nil!=nnn){
//        NSArray *vvv = nnn.viewControllers;
//        if(nil!=vvv){
//            if(vvv.count>1){
//                [self.navigationController popViewControllerAnimated:YES];
//                flagIsLast=NO;
//            }
//        }
//        if(flagIsLast==YES){
//            NSLog(@" flagIsLast==YES for navigationController");
//        }
//    }else{
//        UIViewController *rootUi=ddd.window.rootViewController;
//        if (rootUi == self){
//            NSLog(@" flagIsLast==YES for rootViewController root = self");
//            flagIsLast=YES;
//        }else{
//            [self dismissViewControllerAnimated:YES completion:^{
//                NSLog(@"Current View dismissViewControllerAnimated");
//            }];
//            [self trigger:WtfEventWhenClose :self.responseData];
//            return;
//        }
//    }
//    //ERROR, should not trigger event here !! should using event => closeUi() action.
//    [self trigger:WtfEventWhenClose :self.responseData];
     [self trigger:WtfEventWhenClose :self.responseData];
}


//- (void) evalJs :(NSString *)js_s
//{
//    NSLog(@": evalJs() should be overrided by descendants");
//}

/* About FullScreen (hide top status bar)
 // Plan A, It works for iOS 5 and iOS 6 , but not in iOS 7.
 // [UIApplication sharedApplication].statusBarHidden = YES;
 // Info.plist need add:
 //    <key>UIStatusBarHidden</key>
 //    <true/>
 // Plan B: Info.plist
 //    <key>UIViewControllerBasedStatusBarAppearance</key>
 //    <false/>
 //    [[UIApplication sharedApplication] setStatusBarHidden:YES
 //                                            withAnimation:UIStatusBarAnimationFade];
 
 //@ref http://stackoverflow.com/questions/18979837/how-to-hide-ios-status-bar
 */

-(void) hideTopStatusBar
{
    [[UIApplication sharedApplication] setStatusBarHidden:YES withAnimation:UIStatusBarAnimationNone];
}
-(void) showTopStatusBar
{
    [[UIApplication sharedApplication] setStatusBarHidden:NO withAnimation:UIStatusBarAnimationNone];
}
-(void) hideTopBar
{
    [[self navigationController] setNavigationBarHidden:YES animated:NO];
}
-(void) showTopBar
{
    [[self navigationController] setNavigationBarHidden:NO animated:NO];
}

-(void) resetTopBarStatus
{
    JSO *param =self.uiData;
    JSO *topbarmode=[param getChild:@"topbar"];
    NSString *topbarmode_s=[JSO o2s:topbarmode];
    
    [self resetTopBar :topbarmode_s];
    
    NSString *topbar_color=[JSO o2s:[param getChild:@"topbar_color"]];
    if([@"B" isEqualToString:topbar_color]){
        [[UIApplication sharedApplication] setStatusBarStyle:UIStatusBarStyleDefault];
    }else if([@"W" isEqualToString:topbar_color]){
        [[UIApplication sharedApplication] setStatusBarStyle:UIStatusBarStyleLightContent];
    }else{
        //ignore...
    }
}

-(void) resetTopBar :(NSString *)mode
{
    if ([WtfTools isEmptyString:mode])
        mode=@"Y";
    
    if([@"F" isEqualToString:mode]){
        [self hideTopStatusBar];
        [self hideTopBar];
        self.edgesForExtendedLayout=UIRectEdgeAll;
    }
    if([@"M" isEqualToString:mode]){
        [self hideTopStatusBar];
        [self showTopBar];
        self.edgesForExtendedLayout=UIRectEdgeNone;
    }
    if([@"Y" isEqualToString:mode]){
        [self showTopStatusBar];
        [self showTopBar];
        self.edgesForExtendedLayout=UIRectEdgeNone;
    }
    if([@"N" isEqualToString:mode]){
        [self showTopStatusBar];
        [self hideTopBar];
        self.edgesForExtendedLayout=UIRectEdgeAll;
    }
}

//default, can be override
- (void) resetTopBarBtn
{
    NSLog(@"resetTopBarBtn() %@", self.uiName);
    
    self.navigationItem.leftBarButtonItem
    = [[UIBarButtonItem alloc]
       initWithBarButtonSystemItem:UIBarButtonSystemItemReply
       target:self
       action:@selector(closeUi)];
}

//
////for <iOS9 ?
//- (BOOL)prefersStatusBarHidden {
//    return NO;
//}
//
////for <iOS9
//-(UIStatusBarStyle)preferredStatusBarStyle{
//    //return UIStatusBarStyleLightContent;
//    NSLog(@" preferredStatusBarStyle returns UIStatusBarStyleDefault");
//    return UIStatusBarStyleDefault;
//}

- (void) setTopBarTitle :(NSString *)title
{
    self.title=title;
}

//---------------- UIViewController: ----------

-(void) viewWillAppear:(BOOL)animated
{
    [super viewWillAppear:animated];    
    [self trigger:WtfEventBeforeDisplay];
}

-(void) viewDidLoad
{
    [super viewDidLoad];
    [self initUi];
}

- (BOOL)prefersStatusBarHidden {
    return NO;
}

-(UIStatusBarStyle)preferredStatusBarStyle{
    //return UIStatusBarStyleLightContent;
    return UIStatusBarStyleDefault;
}

//---------------- Spinner: ----------

- (void) spinnerInit
{
    //INIT SPIN
    //UIActivitymyIndicatorView *
    _myIndicatorView = [[UIActivityIndicatorView alloc] initWithActivityIndicatorStyle:UIActivityIndicatorViewStyleWhiteLarge];
    _myIndicatorView.backgroundColor = [[UIColor blackColor] colorWithAlphaComponent:0.5];
    _myIndicatorView.color =[UIColor whiteColor];
    _myIndicatorView.layer.cornerRadius = 5;
    _myIndicatorView.layer.masksToBounds = TRUE;
    
    _myIndicatorView.autoresizingMask = UIViewAutoresizingFlexibleLeftMargin
    //| UIViewAutoresizingFlexibleWidth
    | UIViewAutoresizingFlexibleRightMargin
    | UIViewAutoresizingFlexibleTopMargin
    //| UIViewAutoresizingFlexibleHeight
    | UIViewAutoresizingFlexibleBottomMargin
    ;
    
    //_myIndicatorView.translatesAutoresizingMaskIntoConstraints = NO;
    
    [_myIndicatorView setHidesWhenStopped:YES];
    _myIndicatorView.center=self.view.center;
    
    [self.view addSubview:_myIndicatorView];
}

- (void) spinnerOn
{
    [_myIndicatorView startAnimating];
}
- (void) spinnerOff
{
    [_myIndicatorView stopAnimating];
}

@end
