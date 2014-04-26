var lanepos = [431, 454, 472, 495, 513, 536, 554, 577];

var Math = Java.type("java.lang.Math");

t = 0;

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

function drawJudge(state, delay, combo){
	// field x is 431, width is 182
	var cs = String(combo);
	switch(state){
	case 1:	//POOR
	case 2:
	case 3:
		if(r.getRenderTime() % 3 < 2) r.drawImage(img2,431 + 54,235,74,34,320,216);
		break;
	case 4:	//BAD
		if(r.getRenderTime() % 3 < 2) r.drawImage(img2,431 + 63,235,56,34,320,180);
		break;
	case 5:	//GOOD
		var x = 431 + (182 - (74 + 4 + cs.length * 20)) / 2;
		if(r.getRenderTime() % 3 < 2){
			r.drawImage(img2,x,235,74,34,320,144);
			drawInteger(img2, combo, x + 74 + 4 - 1, 235, 413, 108, 220, 34, -2, false);
		}
		break;
	case 6:	//GREAT
		var x = 431 + (182 - (92 + 4 + cs.length * 20)) / 2;
		if(r.getRenderTime() % 3 < 2){
			r.drawImage(img2,x,235,92,34,320,108);
			drawInteger(img2, combo, x + 92 + 4 - 1, 235, 413, 108, 220, 34, -2, false);
		}
		break;
	case 7:	//PGREAT
		var x = 431 + (182 - (92 + 4 + cs.length * 20)) / 2;
		r.drawImage(img2,x,235,92,34,320,Math.floor((r.getRenderTime() % 6) / 2) * 36);
		drawInteger(img2, combo, x + 92 + 4 - 1, 235, 413, Math.floor((r.getRenderTime() % 6) / 2) * 36, 220, 34, -2, false);
		break;
	}
}

function init(){
	img = new Image(getPath("frame.png"));
	img2 = new Image(getPath("parts.png"));
}

function render(){
	r.drawImage(img,431,0,182,320,624,0);	//playfield
	
	//r.setColor(1, 1, 1, 0.2);
	r.drawImage(img2,431,308,182,12,446,144);	//beat
	
	//r.setColor(1, 1, 1, 1);
	r.drawImage(img,431,316,182,4,440,322);	//judgeline
	
	
	
	for each (var e in s.getPlayer().getActiveNoteList()) drawNote(e.getLane(), e.getPosition());
	
	r.drawImage(img,422,0,218,480,220,0);	//playframe
	
	r.drawImage(img,326,0,96,480,0,488);	//score
	r.drawImage(img,0,0,326,480,328,976);	//movie
	
	for(var i=0;i<39;i++){
		r.drawImage(img2,430+4*i,394,4,14,0,115);	//blue gauge
	}
	for(var i=39;i<50;i++){
		r.drawImage(img2,430+4*i,394,4,14,5,115);	//red gauge
	}
	
	if(s.getPlayer().getShowJudgeTimer() != 0) drawJudge(s.getPlayer().getLastJudgeState(), s.getPlayer().getLastJudgeDelay(), s.getPlayer().getCombo());
	
	//drawNote(0,1);
	
	drawInteger(img2, 100, 421, 374, 119, 99, 190, 16, 0, false);	//gauge
	drawInteger(img2, 123456, 145, 47, 119, 131, 190, 16, 0, true);	//score
	drawInteger(img2, 7890, 183, 69, 119, 131, 190, 16, 0, true);	//combo
}

function drawInteger(img, num, dx, dy, sx, sy, sw, sh, padding, format){
	var n = String(num);//.split('');
	var cw = sw / 10;
	for(var i=0;i<n.length;i++){
		r.drawImage(img, dx+i*(cw+padding), dy, cw, sh, sx+(n.charCodeAt(i)-48)*cw, sy);
	}
}