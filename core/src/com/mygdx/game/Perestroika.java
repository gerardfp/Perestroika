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
    Texture mosca, hoja, moneda, agua, cruz;

    int filas = 5;
    int columnas = 10;

    float[][] tamaños = new float[filas][columnas];
    float[][] tiempos = new float[filas][columnas];
    float[][] cronos = new float[filas][columnas];
    int[][] estados = new int[filas][columnas];

    float anchoColumna;
    float altoFila;

    int moscaX, moscaY;
    int estadoMosca = 1;
    float cronoMuerte;

    int level = 1;

    int porcentageHojasInicial;

    int tamañoHojaMaximo;
    int tamañoHojaMinimo;

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

        mosca = new Texture("mosca.png");
        hoja = new Texture("hoja.png");
        moneda = new Texture("moneda.png");
        agua = new Texture("agua.png");
        cruz = new Texture("cruz.png");

        resetGame();
    }

    void resetGame(){
        filas = 4;
        columnas = 7;

        anchoColumna = 600f/(columnas+1);
        altoFila = 400f/(filas+1);

        tamañoHojaMaximo = (int) (anchoColumna*0.66f);
        tamañoHojaMinimo = (int) (anchoColumna*0.33f);

        tiempoDisminucionHojaMaximo = 2.5f;
        tiempoDisminucionHojaMinimo = 0.5f;

        tiempoReaparicionHojaMaximo = 5f;
        tiempoReaparicionHojaMinimo = 0.75f;

        porcentageHojasInicial = 66;

        for (int i = 0; i < filas; i++) {
            for (int j = 0; j < columnas; j++) {
                if(random.nextInt(100) < porcentageHojasInicial) {
                    tamaños[i][j] = random.nextInt(tamañoHojaMaximo-tamañoHojaMinimo) + tamañoHojaMinimo;
                    tiempos[i][j] = random.nextFloat() * (tiempoDisminucionHojaMaximo - tiempoDisminucionHojaMinimo) + tiempoDisminucionHojaMinimo;
                    cronos[i][j] = tiempos[i][j];
                    estados[i][j] = 2;
                } else {
                    tiempos[i][j] = random.nextFloat() * (tiempoDisminucionHojaMaximo - tiempoDisminucionHojaMinimo) + tiempoDisminucionHojaMinimo;
                    cronos[i][j] = tiempos[i][j];
                    estados[i][j] = 0;
                }
            }
        }

        tamaños[0][0] = tamañoHojaMaximo;
        tiempos[0][0] = tiempoDisminucionHojaMaximo;
        cronos[0][0] = tiempos[0][0];
        estados[0][0] = 2;

        tamaños[filas-1][columnas-1] = tamañoHojaMaximo;
        tiempos[filas-1][columnas-1] = Float.POSITIVE_INFINITY;
        cronos[filas-1][columnas-1] = tiempos[filas-1][columnas-1];
        estados[filas-1][columnas-1] = 2;

        moscaX = 0;
        moscaY = 0;
        estadoMosca = 1;
    }

    void respawn(){
        resetGame();  // ejemmm
    }

    void levelUp(){
        level++;

        resetGame(); // ejemmm
    }

    @Override
    public void render () {
        Gdx.gl.glClearColor(0.33f, 0.33f, 0.66f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        if(estadoMosca == 1) {

            for (int i = 0; i < filas; i++) {
                for (int j = 0; j < columnas; j++) {

                    cronos[i][j] -= Gdx.graphics.getDeltaTime();

                    if (cronos[i][j] < 0) {

                        tamaños[i][j] -= 2;

                        if (estados[i][j] == 2 && tamaños[i][j] < tamañoHojaMinimo) {
                            tiempos[i][j] = 0.3f;
                            estados[i][j] = 1;
                        } else if (estados[i][j] == 1) {
                            tiempos[i][j] = random.nextFloat() * (tiempoReaparicionHojaMaximo - tiempoReaparicionHojaMinimo) + tiempoReaparicionHojaMinimo;
                            estados[i][j] = 0;
                        } else if (estados[i][j] == 0) {
                            tiempos[i][j] = random.nextFloat() * (tiempoDisminucionHojaMaximo - tiempoDisminucionHojaMinimo) + tiempoDisminucionHojaMinimo;
                            tamaños[i][j] = random.nextInt(tamañoHojaMaximo-tamañoHojaMinimo) + tamañoHojaMinimo;
                            estados[i][j] = 2;
                        }

                        cronos[i][j] = tiempos[i][j];
                    }
                }
            }

            if (Gdx.input.isKeyJustPressed(Input.Keys.LEFT)       && moscaX > 0)            moscaX--;
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
                    batch.draw(agua, 20 + anchoColumna*(j+1)-13, 40 + altoFila*(i+1)-7);
                }
            }
        }

        batch.draw(moneda, 20 + anchoColumna *columnas - 15, 40+altoFila*filas-7);
        font.draw(batch, String.valueOf(level), 20 + anchoColumna*columnas - (level < 10 ? 5 : 9), 40+altoFila*filas+10 );

        if(estadoMosca == 1) {
            batch.draw(mosca, 20 + anchoColumna * (moscaX + 1) - 8, 40 + altoFila * (moscaY + 1) - 4);
        } else if(estadoMosca == 0){
            batch.draw(cruz, 20 + anchoColumna * (moscaX + 1) - 20, 40 + altoFila * (moscaY + 1) - 23);
        }

        batch.end();
    }
}
