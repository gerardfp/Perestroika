package com.mygdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import java.util.Random;


public class Perestroika extends ApplicationAdapter {
    Random random;
    SpriteBatch batch;
    BitmapFont font;
    Texture start, mosca, hoja, moneda, agua, cruz, back, life;
    String dir = "new/";

    int pantalla;

    int filas;
    int columnas;

    float[][] tamaños;
    float[][] menguas;
    int[][] estados;

    float anchoColumna;
    float altoFila;

    int moscaX, moscaY;
    int estadoMosca;

    int level;

    int lifes;

    int porcentageHojasInicial;

    float tamañoHojaMaximo;
    float tamañoHojaMinimo;

    float menguaHojaMaximo;
    float menguaHojaMinimo;

    float tiempoReaparicionMaximo;
    float tiempoReaparicionMinimo;
    float alarmaReaparicion;

    float tiempoJuego;
    float alarmaMengua;
    float duracionAlarmaMengua;

    float cronoMuerte;

    @Override
    public void create () {
        random = new Random();
        batch = new SpriteBatch();
        font = new BitmapFont();
        font.setColor(Color.BLACK);

        start = new Texture(dir+"start.png");
        back = new Texture(dir+"back.png");
        mosca = new Texture(dir+"rana.png");
        hoja = new Texture(dir+"hoja.png");
        moneda = new Texture(dir+"moneda.png");
        agua = new Texture(dir+"agua.png");
        cruz = new Texture(dir+"cruz.png");
        life = new Texture(dir+"life.png");


        resetGame();
    }

    void resetGame(){

        level = 1;

        lifes = 3;

        filas = 4;
        columnas = 7;

        menguaHojaMaximo = 200;
        menguaHojaMinimo = 40;

        tiempoReaparicionMaximo = 0.5f;
        tiempoReaparicionMinimo = 0.05f;

        porcentageHojasInicial = 66;

        tiempoJuego = 0f;
        duracionAlarmaMengua = 0.3f;
        alarmaMengua = duracionAlarmaMengua;

        respawn();
    }

    void levelUp(){
        level++;

        filas += 0.5f;
        columnas += 0.5f;

        porcentageHojasInicial -= 3;

        respawn();
    }

    void respawn(){
        tamaños = new float[filas][columnas];
        menguas = new float[filas][columnas];
        estados = new int[filas][columnas];

        anchoColumna = 600f/(columnas+1);
        altoFila = 400f/(filas+1);

        tamañoHojaMaximo = anchoColumna*0.9f;
        tamañoHojaMinimo =  anchoColumna*0.33f;

        for (int i = 0; i < filas; i++) {
            for (int j = 0; j < columnas; j++) {
                if(random.nextInt(100) < porcentageHojasInicial) {
                    tamaños[i][j] = random(tamañoHojaMinimo, tamañoHojaMaximo);
                    menguas[i][j] = random(menguaHojaMinimo, menguaHojaMaximo);
                    estados[i][j] = 2;
                } else {
                    estados[i][j] = 0;
                }
            }
        }

        tamaños[0][0] = tamañoHojaMaximo;
        menguas[0][0] = menguaHojaMinimo;
        estados[0][0] = 2;

        tamaños[filas-1][columnas-1] = tamañoHojaMaximo;
        menguas[filas-1][columnas-1] = 0;
        estados[filas-1][columnas-1] = 2;

        moscaX = 0;
        moscaY = 0;
        estadoMosca = 1;
    }



    @Override
    public void render () {
        Gdx.gl.glClearColor(0.4f, 0.79f, 0.88f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        if(pantalla == 0){
            if(Gdx.input.isKeyJustPressed(Input.Keys.ANY_KEY)){
                pantalla = 1;
            }
            batch.begin();
            batch.draw(start, 0,0);
            batch.end();

            return;
        }

        if(estadoMosca == 1) {

            tiempoJuego += Gdx.graphics.getDeltaTime();

            if(alarmaMengua < tiempoJuego) {
                alarmaMengua = tiempoJuego + duracionAlarmaMengua;

                for (int i = 0; i < filas; i++) {
                    for (int j = 0; j < columnas; j++) {
                        if(estados[i][j] == 2) {
                            tamaños[i][j] -= menguas[i][j] * Gdx.graphics.getDeltaTime();

                            if (tamaños[i][j] < tamañoHojaMinimo) {
                                estados[i][j] = 1;
                            }
                        } else if(estados[i][j] == 1){
                            estados[i][j] = 0;
                        }
                    }
                }
            }

            if(alarmaReaparicion < tiempoJuego){
                alarmaReaparicion = tiempoJuego + random(tiempoReaparicionMinimo, tiempoReaparicionMaximo);
                int startFila = random.nextInt(filas);
                int startColumna = random.nextInt(columnas);
                out:
                for (int ci = startFila; ci < startFila + filas; ci++) {
                    for (int cj = random.nextInt(columnas); cj < startColumna + columnas; cj++) {
                        int i = ci%filas;
                        int j = cj%columnas;
                        if (estados[i][j] == 0) {
                            tamaños[i][j] = random(tamañoHojaMinimo, tamañoHojaMaximo);
                            menguas[i][j] = random(menguaHojaMinimo, menguaHojaMaximo);
                            estados[i][j] = 2;
                            break out;
                        }
                    }
                }
            }

            if      (Gdx.input.isKeyJustPressed(Input.Keys.LEFT)  && moscaX > 0)            moscaX--;
            else if (Gdx.input.isKeyJustPressed(Input.Keys.RIGHT) && moscaX < columnas - 1) moscaX++;
            else if (Gdx.input.isKeyJustPressed(Input.Keys.DOWN)  && moscaY > 0)            moscaY--;
            else if (Gdx.input.isKeyJustPressed(Input.Keys.UP)    && moscaY < filas - 1)    moscaY++;


            if(moscaX == columnas-1 && moscaY == filas-1){
                levelUp();
            }

            if (estados[moscaY][moscaX] != 2) {
                estados[moscaY][moscaX] = 1;
                estadoMosca = 0;
                lifes--;
                cronoMuerte = 1f;
            }

        } else if (estadoMosca == 0){

            cronoMuerte -= Gdx.graphics.getDeltaTime();

            if(cronoMuerte < 0){
                respawn();
            }
        }

        batch.begin();

        batch.draw(back, 0, 0);

        for (int i = 0; i < filas; i++) {
            for (int j = 0; j < columnas; j++) {
                if(estados[i][j] == 2) {
                    batch.draw(hoja, 20 + anchoColumna*(j+1)-tamaños[i][j]/2, 40 + altoFila*(i+1)-tamaños[i][j]/4, tamaños[i][j], tamaños[i][j]/2);
                } else if(estados[i][j] == 1) {
                    batch.draw(agua, 20 + anchoColumna*(j+1)-tamañoHojaMinimo/2, 40 + altoFila*(i+1)-tamañoHojaMinimo/4, tamañoHojaMinimo, tamañoHojaMinimo/2);
                }
            }
        }

        batch.draw(moneda, 20 + anchoColumna *columnas - 12, 40+altoFila*filas, 24, 24);
        font.draw(batch, String.valueOf(level), 20 + anchoColumna*columnas - (level < 10 ? 4 : 8), 40 + altoFila*filas + 20 );

        if(estadoMosca == 1) {
            batch.draw(mosca, 20 + anchoColumna * (moscaX + 1) - 12, 40 + altoFila * (moscaY + 1), 24, 24);
        } else if(estadoMosca == 0){
            batch.draw(cruz, 20 + anchoColumna * (moscaX + 1) - 15, 40 + altoFila * (moscaY + 1) - 20, 30, 45);
        }

        for (int i = 0; i < 3; i++) {
            if(lifes > i){
                batch.draw(life, 30+i*44, 442, 32, 28);
            }
        }
        batch.end();
    }

    float random(float min, float max){
        return random.nextFloat() * (max - min) + min;
    }
}

// #67cbe2  0.4f, 0.79f, 0.88f
// #b5e3fa  0.7f, 0.88f, 0.97f