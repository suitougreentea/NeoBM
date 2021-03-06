var lanepos = [431, 454, 472, 495, 513, 536, 554, 577];

var Math = Java.type("java.lang.Math");
var EventNote = Java.type("io.github.suitougreentea.NeoBM.NBM.sequence.EventNote");
var EventLongNote = Java.type("io.github.suitougreentea.NeoBM.NBM.sequence.EventLongNote");

var DigitFont = Java.type("io.github.suitougreentea.NeoBM.player.DigitFont");

function init(){
	img = new Image(getPath("frame.png"));
	img2 = new Image(getPath("parts.png"));
	outlineSmallFont = new AngelCodeFont(r,getPath("bitmap.fnt"));
	gaugeFont = new DigitFont(r,img2,100,100,19,16,19,0);
	scoreFont = new DigitFont(r,img2,100,132,19,16,19,0);
	
	comboFont = [];
	for(var i=0;i<4;i++){
		comboFont[i] = new DigitFont(r,img2,414,i*36,22,34,20,0);
	}
}

//t = 0;

// pos: 0-1(float)
function drawNote(lane, pos){
	switch(lane){
	case 7:
		r.drawImage(img2,lanepos[lane],Math.floor((pos*320)-6),36,6,0,0);
		break;
	case 0:
	case 2:
	case 4:
	case 6:
		r.drawImage(img2,lanepos[lane],Math.floor((pos*320)-6),21,6,74,0);
		break;
	case 1:
	case 3:
	case 5:
		r.drawImage(img2,lanepos[lane],Math.floor((pos*320)-6),16,6,96,0);
		break;
	}
}

function drawLongNote(lane, pos, endpos, active){
	var x = lanepos[lane];
	var starty = Math.floor((pos*320)-6)
	var endy = Math.floor((endpos*320)-6)
	var height = starty - endy;
	var srcy = active ? 15 : 19;
	switch(lane){
	case 7:
		r.drawImage(img2,x,starty,36,6,0,7);
		r.drawImage(img2,x,endy,36,height,0,srcy,36,1);
		r.drawImage(img2,x,endy,36,6,0,22);
		break;
	case 0:
	case 2:
	case 4:
	case 6:
		r.drawImage(img2,x,starty,21,6,74,7);
		r.drawImage(img2,x,endy,21,height,74,srcy,21,1);
		r.drawImage(img2,x,endy,21,6,74,22);
		break;
	case 1:
	case 3:
	case 5:
		r.drawImage(img2,x,starty,16,6,96,7);
		r.drawImage(img2,x,endy,16,height,96,srcy,16,1);
		r.drawImage(img2,x,endy,16,6,96,22);
		break;
	}
}

function drawBeam(array){
	for(var i=0;i<8;i++){
		var x = lanepos[i];
		r.setColor(1,1,1,array[i]);
		switch(i){
		case 7:
			r.drawImage(img2,x,83,36,240,634,80);
			break;
		case 0:
		case 2:
		case 4:
		case 6:
			r.drawImage(img2,x,83,21,240,672,80);
			break;
		case 1:
		case 3:
		case 5:
			r.drawImage(img2,x,83,16,240,695,80);
			break;
		}
	}
	r.setColor(1,1,1,1);
}

function drawJudge(state, delay, combo){
	// field x is 431, width is 182
	var cs = String(combo);
	switch(state){
	case 1:	//POOR
	case 2:
	case 3:
		if(r.getRenderTime() % 3 < 2) r.drawImage(img2,431 + 54,235,74,34,320,216);
		break;
	case 4:
	case 5:	//BAD
		if(r.getRenderTime() % 3 < 2) r.drawImage(img2,431 + 63,235,56,34,320,180);
		break;
	case 6:	//GOOD
		var x = 431 + (182 - (74 + 4 + cs.length * 20)) / 2;
		if(r.getRenderTime() % 3 < 2){
			r.drawImage(img2,x,235,74,34,320,144);
			comboFont[3].drawString(combo, x + 74 + 4 - 1, 235);
		}
		break;
	case 7:	//GREAT
		var x = 431 + (182 - (92 + 4 + cs.length * 20)) / 2;
		if(r.getRenderTime() % 3 < 2){
			r.drawImage(img2,x,235,92,34,320,108);
			comboFont[3].drawString(combo, x + 92 + 4 - 1, 235);
		}
		break;
	case 8:	//PGREAT
		var x = 431 + (182 - (92 + 4 + cs.length * 20)) / 2;
		r.drawImage(img2,x,235,92,34,320,Math.floor((r.getRenderTime() % 6) / 2) * 36);
		comboFont[Math.floor((r.getRenderTime() % 6) / 2)].drawString(combo, x + 92 + 4 - 1, 235);
		break;
	}
	
	if(delay!=0 && state!=8){
		if(delay > 0){
			r.setColor(1,0,0,1);
			outlineSmallFont.drawString("+" + delay + "ms",500,215);
		}else{
			r.setColor(0,0,1,1);
			outlineSmallFont.drawString(delay + "ms",500,215);
		}
		r.setColor(1,1,1,1);
	}
}

function render(){
	r.drawImage(img,431,0,182,320,624,0);	//playfield
	
	//r.setColor(1, 1, 1, (2 - s.getPlayer().getBeatRate()) * 0.5);
	r.drawImage(img2,431,308 + (s.getPlayer().getBeatRate() * 6),182,12,446,144);	//beat
	//r.setColor(1, 1, 1, 1);
	
	r.drawImage(img,431,316,182,4,440,322);	//judgeline
	
	drawBeam(s.getPlayer().getKeyBeam());
	
	for each (var e in s.getPlayer().getActiveNoteList()){
		if(e instanceof EventLongNote) drawLongNote(e.getLane(), e.getPosition(), e.getEndPosition(), e.isActive());
		else drawNote(e.getLane(), e.getPosition());
	}
	
	r.drawImage(img,422,0,218,480,220,0);	//playframe
	
	r.drawImage(img,326,0,96,480,0,488);	//score
	r.drawImage(img,0,0,326,480,328,976);	//movie
	
	for(var i=0;i<s.getPlayer().getCalculatedGauge() / 2;i++){
		//r.drawImage(img2,430+4*i,394,4,14,0,115);	//blue gauge
		r.drawImage(img2,430+4*i,394,4,14,5,115);	//red gauge
	}
	/*for(var i=39;i<50;i++){
		r.drawImage(img2,430+4*i,394,4,14,5,115);	//red gauge
	}*/
	
	if(s.getPlayer().getShowJudgeTimer() != 0) drawJudge(s.getPlayer().getLastJudgeState(), s.getPlayer().getLastJudgeDelay(), s.getPlayer().getCombo());
	
	//drawNote(0,1);
	
	gaugeFont.drawStringRight(s.getPlayer().getCalculatedGauge(), 477, 374);	//gauge
	r.drawImage(img2, 479,382,7,7,310,108);	//%
	scoreFont.drawString(paddingLeft(0,' ',6), 145, 47);	//score
	scoreFont.drawString(paddingLeft(0,' ',4), 183, 69);	//combo
	
	r.setColor(1,1,1,0.9);
	r.drawImage(img2,261,376,61,98,400,160);
	r.setColor(1,1,1,1);
	outlineSmallFont.drawString("PG:    0",261+3,376+7);
	outlineSmallFont.drawString("GR:    0",261+3,376+20);
	outlineSmallFont.drawString("GD:    0",261+3,376+33);
	outlineSmallFont.drawString("BD:    0",261+3,376+46);
	outlineSmallFont.drawString("PR:    0",261+3,376+59);
	outlineSmallFont.drawString("CB:    0",261+3,376+72);
}

function paddingLeft(val,char,n){
	var leftval = "";
	for(;leftval.length < n;leftval+=char);
	return (leftval+val).slice(-n);
}