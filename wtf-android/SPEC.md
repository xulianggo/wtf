# Protocol Specification

dont' use callNative

```
.callHandler(handlerName, callData, function(responseData){});

.registerHandler(handlerName, function(callData){});
```

### Data Structure

```
callMsg:{
	callbackId
	callTime //for housekeeping & benchmark
	callData
}

callbackMsg:{
	responseId
	responseData
}
```
