attribute vec4 v_Position;
attribute vec4 a_Color;
varying   vec4 v_Color;

void main(){
  gl_Position = v_Position;
  v_Color = a_Color;
  gl_PointSize=20.0;
}


