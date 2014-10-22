var myVideo={};
myVideo.playPause=function(){ 
	if (myVideo.vid.paused) 
	myVideo.vid.play(); 
	else 
	myVideo.vid.pause(); 
} 
myVideo.makeBig=function(){ 
	myVideo.vid.width=800; 
} 
myVideo.makeSmall=function(){ 
	myVideo.vid.width=320; 
} 
myVideo.makeNormal=function(){ 
	myVideo.vid.width=600; 
}
myVideo.toggleMute=function(){
	myVideo.vid.muted = !myVideo.vid.muted;
}
myVideo.setVolume=function(val) { 
	if (val!=0 || val!=undefined){
		try{
			myVideo.vid.volume = myVideo.vid.volume + (val/100);
		}catch(err){/*swallowed*/}
	}
	console.log("Volume: " + myVideo.vid.volume); 
};