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


public class PerestroikaTimed extends ApplicationAdapter {
    Random random;
    SpriteBatch batch;
    BitmapFont font;
    Texture start, mosca, hoja, moneda, agua, cruz;
    String dir = "new/";

    int pantalla;

    int filas;
    int columnas;

    float[][] tamaños;
    float[][] disminu;
    float[][] tiempos;
    float[][] cronos;
    int[][] estados;

    float anchoColumna;
    float altoFila;

    int moscaX, moscaY;
    int estadoMosca;
    float cronoMuerte;

    int level;

    int porcentageHojasInicial;

    float tamañoHojaMaximo;
    float tamañoHojaMinimo;

    float tiempoDisminucionHojaMaximo;
    float tiempoDisminucionHojaMinimo;

    float tiempoReaparicionHojaMaximo;
    float tiempoReaparicionHojaMinimo;

    @Override
    public void create () {
        random = new Random();
        batch = new SpriteBatch();
        font = new BitmapFont();
        font.setColor(Color.BLACK);

        mosca = new Texture(dir+"rana.png");
        hoja = new Texture(dir+"hoja.png");
        moneda = new Texture(dir+"moneda.png");
        agua = new Texture(dir+"agua.png");
        cruz = new Texture(dir+"cruz.png");
        start = new Texture(dir+"start.png");

        resetGame();
    }

    void resetGame(){

        level = 1;

        filas = 4;
        columnas = 7;

        tiempoDisminucionHojaMaximo = 2.5f;
        tiempoDisminucionHojaMinimo = 0.5f;

        tiempoReaparicionHojaMaximo = 5f;
        tiempoReaparicionHojaMinimo = 0.75f;

        porcentageHojasInicial = 66;

        respawn();

    }

    void levelUp(){
        level++;

        filas += 0.5f;
        columnas += 0.5f;

        tiempoDisminucionHojaMaximo -= 0.2f;
        tiempoReaparicionHojaMinimo -= 0.05f;

        tiempoReaparicionHojaMaximo += 0.1f;
        tiempoReaparicionHojaMinimo += 0.05f;

        porcentageHojasInicial -= 3;

        respawn(); // ejemmm
    }

    void respawn(){
        tamaños = new float[filas][columnas];
        disminu = new float[filas][columnas];
        tiempos = new float[filas][columnas];
        cronos = new float[filas][columnas];
        estados = new int[filas][columnas];

        anchoColumna = 600f/(columnas+1);
        altoFila = 400f/(filas+1);

        tamañoHojaMaximo = anchoColumna*0.66f;
        tamañoHojaMinimo =  anchoColumna*0.33f;

        for (int i = 0; i < filas; i++) {
            for (int j = 0; j < columnas; j++) {
                if(random.nextInt(100) < porcentageHojasInicial) {
                    tamaños[i][j] = random.nextInt((int)(tamañoHojaMaximo-tamañoHojaMinimo)) + tamañoHojaMinimo;
                    disminu[i][j] = random.nextInt(5) + 1;
                    tiempos[i][j] = random.nextFloat() * (tiempoDisminucionHojaMaximo - tiempoDisminucionHojaMinimo) + tiempoDisminucionHojaMinimo;
                    cronos[i][j] = tiempos[i][j];
                    estados[i][j] = 2;
                } else {
                    tiempos[i][j] = random.nextFloat() * (tiempoReaparicionHojaMaximo - tiempoReaparicionHojaMinimo) + tiempoReaparicionHojaMinimo;
                    cronos[i][j] = tiempos[i][j];
                    estados[i][j] = 0;
                }
            }
        }

        tamaños[0][0] = tamañoHojaMaximo;
        disminu[0][0] = random.nextInt(5) + 1;
        tiempos[0][0] = tiempoDisminucionHojaMaximo;
        cronos[0][0] = tiempos[0][0];
        estados[0][0] = 2;

        tamaños[filas-1][columnas-1] = tamañoHojaMaximo;
        disminu[filas-1][columnas-1] = 0;
        tiempos[filas-1][columnas-1] = Float.POSITIVE_INFINITY;
        cronos[filas-1][columnas-1] = tiempos[filas-1][columnas-1];
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

            for (int i = 0; i < filas; i++) {
                for (int j = 0; j < columnas; j++) {

                    cronos[i][j] -= Gdx.graphics.getDeltaTime();

                    if (cronos[i][j] < 0) {

                        tamaños[i][j] -= 2;

                        if (estados[i][j] == 2 && tamaños[i][j] < tamañoHojaMinimo) {
                            tiempos[i][j] = 1.3f;
                            estados[i][j] = 1;
                        } else if (estados[i][j] == 1) {
                            tamaños[i][j] = tamañoHojaMaximo;
                            tiempos[i][j] = random.nextFloat() * (tiempoReaparicionHojaMaximo - tiempoReaparicionHojaMinimo) + tiempoReaparicionHojaMinimo;
                            estados[i][j] = 0;
                        } else if (estados[i][j] == 0  && tamaños[i][j] < tamañoHojaMinimo) {
                            tamaños[i][j] = random.nextInt((int)(tamañoHojaMaximo-tamañoHojaMinimo)) + tamañoHojaMinimo;
                            disminu[i][j] = random.nextInt(5) + 1;
                            tiempos[i][j] = random.nextFloat() * (tiempoDisminucionHojaMaximo - tiempoDisminucionHojaMinimo) + tiempoDisminucionHojaMinimo;

                            estados[i][j] = 2;
                        }

                        cronos[i][j] = tiempos[i][j];
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
                cronoMuerte = 1f;
            }

        } else if (estadoMosca == 0){

            cronoMuerte -= Gdx.graphics.getDeltaTime();

            if(cronoMuerte < 0){
                respawn();
            }
        }

        batch.begin();

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
        font.draw(batch, String.valueOf(level), 20 + anchoColumna*columnas - (level < 10 ? 3 : 8), 40 + altoFila*filas + 20 );

        if(estadoMosca == 1) {
            batch.draw(mosca, 20 + anchoColumna * (moscaX + 1) - 12, 40 + altoFila * (moscaY + 1), 24, 24);
        } else if(estadoMosca == 0){
            batch.draw(cruz, 20 + anchoColumna * (moscaX + 1) - 15, 40 + altoFila * (moscaY + 1) - 20, 30, 45);
        }

        batch.end();
    }
}

// #67cbe2  0.4f, 0.79f, 0.88f
// #b5e3fa  0.7f, 0.88f, 0.97f