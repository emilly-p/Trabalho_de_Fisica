import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;

public class Main {
    private float chaveY = 2.5f;
    private float velY = 0.0f;
    private float gravidade = -0.0001f; // Queda bem lenta
    private float tempo = 0.0f;
    private boolean noBarco = false;

    public void run() {
        if (!glfwInit()) return;
        long window = glfwCreateWindow(1024, 768, "Barquinho Charmoso 2D", 0, 0);
        glfwMakeContextCurrent(window);
        GL.createCapabilities();
        glfwSwapInterval(1);

        while (!glfwWindowShouldClose(window)) {
            render();
            glfwSwapBuffers(window);
            glfwPollEvents();
        }
        glfwTerminate();
    }

    private void render() {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        glLoadIdentity();
        glOrtho(-4, 4, -3, 3, -1, 1); // Define nosso espaço 2D
        tempo += 0.02f;

        desenharFundo();
        
        // Movimento do barco (balanço suave)
        float balancoY = (float) Math.sin(tempo) * 0.1f;
        float balancoRot = (float) Math.sin(tempo * 0.8f) * 2.0f;

        // --- DESENHAR BARCO ---
        glPushMatrix();
            glTranslatef(0, balancoY - 0.5f, 0);
            glRotatef(balancoRot, 0, 0, 1);
            desenharBarcoDetalhado();
        glPopMatrix();

        // --- LÓGICA DA CHAVE ---
        float alturaBarco = balancoY - 0.1f; // Ajuste para a chave cair "dentro" do barco
        if (!noBarco) {
            velY += gravidade;
            chaveY += velY;
            if (chaveY <= alturaBarco) {
                chaveY = alturaBarco;
                noBarco = true;
            }
        } else {
            chaveY = alturaBarco; // Segue o balanço do barco
        }

        // --- DESENHAR CHAVE ---
        glPushMatrix();
            glTranslatef(0, chaveY, 0);
            if (!noBarco) glRotatef(tempo * 50, 0, 0, 1); // Gira enquanto cai
            else glRotatef(balancoRot, 0, 0, 1); // Balança junto com o barco
            desenharChave();
        glPopMatrix();
    }

    private void desenharFundo() {
        // Céu com Degradê (Pôr do Sol)
        glBegin(GL_QUADS);
            glColor3f(0.1f, 0.4f, 0.8f); // Azul topo
            glVertex2f(-4, 3); glVertex2f(4, 3);
            glColor3f(0.9f, 0.6f, 0.4f); // Laranja horizonte
            glVertex2f(4, -0.5f); glVertex2f(-4, -0.5f);
        glEnd();

        // Sol
        glPushMatrix();
            glTranslatef(-2.5f, 1.8f, 0);
            glColor3f(1.0f, 0.9f, 0.0f);
            desenharCirculo(0.4f);
        glPopMatrix();

        // Mar Animado
        glColor3f(0.0f, 0.3f, 0.6f);
        glBegin(GL_POLYGON);
            glVertex2f(-4, -0.5f);
            for (float x = -4; x <= 4; x += 0.2f) {
                float y = -0.5f + (float) Math.sin(x + tempo) * 0.05f;
                glVertex2f(x, y);
            }
            glVertex2f(4, -3);
            glVertex2f(-4, -3);
        glEnd();
    }

    private void desenharBarcoDetalhado() {
        // Casco
        glColor3f(0.5f, 0.3f, 0.1f);
        glBegin(GL_POLYGON);
            glVertex2f(-1.2f, 0.4f);
            glVertex2f(1.2f, 0.4f);
            glVertex2f(0.8f, -0.2f);
            glVertex2f(-0.8f, -0.2f);
        glEnd();

        // Mastro
        glColor3f(0.3f, 0.2f, 0.1f);
        glRectf(-0.05f, 0.4f, 0.05f, 1.8f);

        // Vela
        glColor3f(0.9f, 0.9f, 0.9f);
        glBegin(GL_TRIANGLES);
            glVertex2f(0.1f, 1.7f);
            glVertex2f(0.1f, 0.6f);
            glVertex2f(1.0f, 0.6f);
        glEnd();
    }

    private void desenharChave() {
        glColor3f(1.0f, 0.8f, 0.0f); // Dourado
        // Cabeça
        glLineWidth(2);
        glBegin(GL_LINE_LOOP);
            for(int i=0; i<360; i+=20) {
                double rad = Math.toRadians(i);
                glVertex2d(Math.cos(rad)*0.15, Math.sin(rad)*0.15 + 0.2);
            }
        glEnd();
        // Haste e dentes
        glBegin(GL_LINES);
            glVertex2f(0, 0.05f); glVertex2f(0, -0.3f); // Haste
            glVertex2f(0, -0.15f); glVertex2f(0.1f, -0.15f); // Dente 1
            glVertex2f(0, -0.25f); glVertex2f(0.1f, -0.25f); // Dente 2
        glEnd();
    }

    private void desenharCirculo(float r) {
        glBegin(GL_POLYGON);
        for(int i=0; i<360; i+=10) {
            double rad = Math.toRadians(i);
            glVertex2d(Math.cos(rad)*r, Math.sin(rad)*r);
        }
        glEnd();
    }

    public static void main(String[] args) {
        new Main().run();
    }
}