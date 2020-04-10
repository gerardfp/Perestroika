package com.mygdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import java.util.Random;


public class Perestroika extends ApplicationAdapter {
    Random random;
    SpriteBatch batch;
    BitmapFont font;
    Texture start, rana, hoja, moneda, agua, cruz, back, life;
    String dir = "new/";

    int pantalla;  int PANTALLA_INICIO = 0; int PANTALLA_JUEGO = 1;    // ver ENUMS

    int filas;
    int columnas;

    float anchoColumna;
    float altoFila;

    float[][] tamañosHojas;
    float[][] menguasHojas;
    float[][] alarmasHojas;
    int[][] estadosHojas;  int HOJA_FLOTANDO = 2; int HOJA_HUNDIENDOSE = 1; int HOJA_HUNDIDA = 0;   // ver ENUMS

    int porcentageHojasInicial;

    float tamañoHojaMaximo;
    float tamañoHojaMinimo;

    float menguaHojaMaximo;
    float menguaHojaMinimo;

    float tiempoReaparicionMaximo;
    float tiempoReaparicionMinimo;

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

        start = new Texture(dir+"start.png");
        back = new Texture(dir+"back.png");
        rana = new Texture(dir+"rana.png");
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

        tiempoReaparicionMaximo = 2.5f;
        tiempoReaparicionMinimo = 0.5f;

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
        estadosHojas = new int[filas][columnas];
        tamañosHojas = new float[filas][columnas];
        menguasHojas = new float[filas][columnas];
        alarmasHojas = new float[filas][columnas];

        anchoColumna = 600f/(columnas+1);
        altoFila = 400f/(filas+1);

        tamañoHojaMaximo = anchoColumna*0.9f;
        tamañoHojaMinimo =  anchoColumna*0.33f;

        for (int i = 0; i < filas; i++) {
            for (int j = 0; j < columnas; j++) {
                if(random.nextInt(100) < porcentageHojasInicial) {
                    estadosHojas[i][j] = HOJA_FLOTANDO;
                    tamañosHojas[i][j] = random(tamañoHojaMinimo, tamañoHojaMaximo);
                    menguasHojas[i][j] = random(menguaHojaMinimo, menguaHojaMaximo);

                } else {
                    estadosHojas[i][j] = HOJA_HUNDIDA;
                    alarmasHojas[i][j] = tiempoJuego + random(tiempoReaparicionMinimo, tiempoReaparicionMaximo);
                }
            }
        }

        estadosHojas[0][0] = HOJA_FLOTANDO;
        tamañosHojas[0][0] = tamañoHojaMaximo;
        menguasHojas[0][0] = menguaHojaMinimo;

        estadosHojas[filas-1][columnas-1] = HOJA_FLOTANDO;
        tamañosHojas[filas-1][columnas-1] = tamañoHojaMaximo;
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
                alarmaMengua = tiempoJuego + duracionAlarmaMengua;

                for (int i = 0; i < filas; i++) {
                    for (int j = 0; j < columnas; j++) {

                        if (estadosHojas[i][j] == HOJA_FLOTANDO) {
                            tamañosHojas[i][j] -= menguasHojas[i][j] * Gdx.graphics.getDeltaTime();

                            if (tamañosHojas[i][j] < tamañoHojaMinimo) {
                                estadosHojas[i][j] = HOJA_HUNDIENDOSE;
                            }

                        } else if (estadosHojas[i][j] == HOJA_HUNDIENDOSE){
                            estadosHojas[i][j] = HOJA_HUNDIDA;
                            alarmasHojas[i][j] = tiempoJuego + random(tiempoReaparicionMinimo, tiempoReaparicionMaximo);

                        } else if (estadosHojas[i][j] == HOJA_HUNDIDA && tiempoJuego > alarmasHojas[i][j]){
                            estadosHojas[i][j] = HOJA_FLOTANDO;
                            tamañosHojas[i][j] = random(tamañoHojaMinimo, tamañoHojaMaximo);
                            menguasHojas[i][j] = random(menguaHojaMinimo, menguaHojaMaximo);
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
                    batch.draw(hoja, 20+anchoColumna*(j+1)-tamañosHojas[i][j]/2, 40+altoFila*(i+1)-tamañosHojas[i][j]/4, tamañosHojas[i][j], tamañosHojas[i][j]/2);
                } else if(estadosHojas[i][j] == HOJA_HUNDIENDOSE) {
                    batch.draw(agua, 20+anchoColumna*(j+1)-tamañoHojaMinimo/2, 40+altoFila*(i+1)-tamañoHojaMinimo/4, tamañoHojaMinimo, tamañoHojaMinimo/2);
                }
            }
        }

        batch.draw(moneda, 20 + anchoColumna *columnas - 12, 40+altoFila*filas, 24, 24);
        font.draw(batch, String.valueOf(level), 20+anchoColumna*columnas-(level < 10 ? 4 : 8), 40+altoFila*filas + 20 );

        if(estadoRana == RANA_VIVA) {
            batch.draw(rana, 20+anchoColumna*(ranaX+1)-12, 40+altoFila*(ranaY+1), 24, 24);
        } else if(estadoRana == RANA_MUERTA){
            batch.draw(cruz, 20+anchoColumna*(ranaX+1)-15, 40+altoFila*(ranaY+1)-15, 30, 45);
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