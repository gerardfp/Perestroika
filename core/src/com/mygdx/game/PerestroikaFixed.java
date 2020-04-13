package com.mygdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import java.util.Random;


public class PerestroikaFixed extends ApplicationAdapter {
    Random random;
    SpriteBatch batch;
    BitmapFont font;
    Texture start, rana, hoja, moneda, agua, cruz, back, life;

    int pantalla;  int PANTALLA_INICIO = 0; int PANTALLA_JUEGO = 1;    // ver ENUMS

    int filas;
    int columnas;

    float[][] tamañosHojas;
    float[][] menguasHojas;
    float[][] alarmasHojas;
    int[][] estadosHojas;  int HOJA_FLOTANDO = 2; int HOJA_HUNDIENDOSE = 1; int HOJA_HUNDIDA = 0;   // ver ENUMS

    float tiempoJuego;
    float alarmaMengua;
    float duracionAlarmaMengua;

    int ranaX, ranaY;
    int estadoRana;   int RANA_VIVA = 1;  int RANA_MUERTA = 2;    // ver ENUMS

    int level;

    int lifes;

    float alarmaRespawn;

    @Override
    public void create () {
        random = new Random();
        batch = new SpriteBatch();
        font = new BitmapFont();
        font.setColor(Color.BLACK);

        start = new Texture("start.png");
        back = new Texture("back.png");
        rana = new Texture("rana.png");
        hoja = new Texture("hoja.png");
        moneda = new Texture("moneda.png");
        agua = new Texture("agua.png");
        cruz = new Texture("cruz.png");
        life = new Texture("life.png");

        resetGame();
    }

    void resetGame(){

        level = 1;

        lifes = 3;

        filas = 3;
        columnas = 5;

        tiempoJuego = 0f;
        alarmaMengua = duracionAlarmaMengua;

        respawn();
    }

    void levelUp(){
        level++;

        respawn();
    }

    void respawn(){
        estadosHojas = new int[filas][columnas];
        tamañosHojas = new float[filas][columnas];
        menguasHojas = new float[filas][columnas];
        alarmasHojas = new float[filas][columnas];

        for (int i = 0; i < filas; i++) {
            for (int j = 0; j < columnas; j++) {
                if(random.nextInt(100) < 66) {
                    estadosHojas[i][j] = HOJA_FLOTANDO;
                    tamañosHojas[i][j] = random(30, 90);
                    menguasHojas[i][j] = random(40, 200);

                } else {
                    estadosHojas[i][j] = HOJA_HUNDIDA;
                    alarmasHojas[i][j] = tiempoJuego + random(0.5f, 2.5f);
                }
            }
        }

        estadosHojas[0][0] = HOJA_FLOTANDO;
        tamañosHojas[0][0] = 90;
        menguasHojas[0][0] = 0.5f;

        estadosHojas[filas-1][columnas-1] = HOJA_FLOTANDO;
        tamañosHojas[filas-1][columnas-1] = 90;
        menguasHojas[filas-1][columnas-1] = 0;

        estadoRana = RANA_VIVA;
        ranaX = 0;
        ranaY = 0;
    }



    @Override
    public void render () {

        // update

        if(pantalla == PANTALLA_INICIO){
            if(Gdx.input.isKeyJustPressed(Input.Keys.ANY_KEY)){
                pantalla = 1;
            }
            batch.begin();
            batch.draw(start, 0,0);
            batch.end();

            return;
        }

        tiempoJuego += Gdx.graphics.getDeltaTime();

        if (estadoRana == RANA_VIVA) {

            if (tiempoJuego > alarmaMengua) {
                alarmaMengua = tiempoJuego + 0.3f;

                for (int i = 0; i < filas; i++) {
                    for (int j = 0; j < columnas; j++) {

                        if (estadosHojas[i][j] == HOJA_FLOTANDO) {
                            tamañosHojas[i][j] -= menguasHojas[i][j] * Gdx.graphics.getDeltaTime();

                            if (tamañosHojas[i][j] < 30) {
                                estadosHojas[i][j] = HOJA_HUNDIENDOSE;
                            }

                        } else if (estadosHojas[i][j] == HOJA_HUNDIENDOSE){
                            estadosHojas[i][j] = HOJA_HUNDIDA;
                            alarmasHojas[i][j] = tiempoJuego + random(0.5f, 2.5f);

                        } else if (estadosHojas[i][j] == HOJA_HUNDIDA && tiempoJuego > alarmasHojas[i][j]){
                            estadosHojas[i][j] = HOJA_FLOTANDO;
                            tamañosHojas[i][j] = random(30, 90);
                            menguasHojas[i][j] = random(40, 200);
                        }
                    }
                }
            }

            if      (Gdx.input.isKeyJustPressed(Input.Keys.LEFT)  && ranaX > 0)            ranaX--;
            else if (Gdx.input.isKeyJustPressed(Input.Keys.RIGHT) && ranaX < columnas - 1) ranaX++;
            else if (Gdx.input.isKeyJustPressed(Input.Keys.DOWN)  && ranaY > 0)            ranaY--;
            else if (Gdx.input.isKeyJustPressed(Input.Keys.UP)    && ranaY < filas - 1)    ranaY++;


            if (ranaX == columnas-1 && ranaY == filas-1){
                levelUp();
            }

            if (estadosHojas[ranaY][ranaX] != HOJA_FLOTANDO) {
                estadosHojas[ranaY][ranaX] = HOJA_HUNDIENDOSE;
                estadoRana = RANA_MUERTA;
                lifes--;
                alarmaRespawn = tiempoJuego + 1f;
            }

        } else if (estadoRana == RANA_MUERTA && tiempoJuego > alarmaRespawn){
            respawn();
        }

        // render

        batch.begin();

        batch.draw(back, 0, 0, 640, 480);

        for (int i = 0; i < filas; i++) {
            for (int j = 0; j < columnas; j++) {
                if(estadosHojas[i][j] == HOJA_FLOTANDO) {
                    batch.draw(hoja, 20+100*(j+1)-tamañosHojas[i][j]/2, 40+100*(i+1)-tamañosHojas[i][j]/4, tamañosHojas[i][j], tamañosHojas[i][j]/2);
                } else if(estadosHojas[i][j] == HOJA_HUNDIENDOSE) {
                    batch.draw(agua, 20+100*(j+1)-30/2, 40+100*(i+1)-30/4, 30, 30/2);
                }
            }
        }

        batch.draw(moneda, 20 + 100*columnas - 12, 40+100*filas, 24, 24);
        font.draw(batch, String.valueOf(level), 20+100*columnas-(level < 10 ? 4 : 8), 40+100*filas + 20 );

        if(estadoRana == RANA_VIVA) {
            batch.draw(rana, 20+100*(ranaX+1)-12, 40+100*(ranaY+1), 24, 24);
        } else if(estadoRana == RANA_MUERTA){
            batch.draw(cruz, 20+100*(ranaX+1)-15, 40+100*(ranaY+1)-15, 30, 45);
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