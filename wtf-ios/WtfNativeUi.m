#import "WtfNativeUi.h"
#import "WtfTools.h"

// father class for scan/gestures

@implementation WtfNativeUi

//---------------- UIViewController: ----------

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    [self trigger:WtfEventMemoryWarning];

//    // Dispose of any resources that can be recreated.
//    [WtfTools quickShowMsgMain:@"Memory Warning" callback:^{
//        //TODO...
//    }];
}

//default no hidden status bar
- (BOOL)prefersStatusBarHidden {
    return NO;
}

//status bar as default
-(UIStatusBarStyle)preferredStatusBarStyle{
    //return UIStatusBarStyleLightContent;
    return UIStatusBarStyleDefault;
}

//------------   <WtfUi> ------------

//default a resetTopBarBtn()
- (void) resetTopBarBtn
{
    NSLog(@"resetTopBarBtn in NativeUi....");
    
    UIBarButtonItem *leftBar
    = [[UIBarButtonItem alloc]
       initWithImage:[UIImage imageNamed:@"btn_nav bar_left arrow"] //@see Images.xcassets
       style:UIBarButtonItemStylePlain
       target:self
       action:@selector(closeUi) //on('click')=>close()
       ];
    leftBar.tintColor = [UIColor blueColor];
    
    self.navigationItem.leftBarButtonItem=leftBar;
    
    //    self.navigationItem.leftBarButtonItem
    //    = [[UIBarButtonItem alloc]
    //       initWithBarButtonSystemItem:UIBarButtonSystemItemReply
    //       target:self
    //       action:@selector(closeUi)];
    //
    //    UIBarButtonItem *rightBtn
    //    = [[UIBarButtonItem alloc]
    //       initWithBarButtonSystemItem:UIBarButtonSystemItemStop target:self action:nil];
    //    self.navigationItem.rightBarButtonItem = rightBtn;
    //[super resetTopBarBtn];
}

//call by viewDidLoad(), child can override!
- (void)initUi
{
    [super initUi];
//
//    self.view.backgroundColor=[UIColor blackColor];
//
//    //self.navigationController.navigationBar.translucent=NO;
//    self.navigationController.navigationBar.backgroundColor=[UIColor blackColor];
//    self.navigationController.navigationBar.tintColor = [UIColor brownColor];
}

@end
