import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*Flores Martínez Paola
  Gonzalez Gonzalez Juan Carlos
  Carmona Viana Israel
*/
public class Scanner {

    private static final Map<String, TipoToken> palabrasReservadas;

    static {
        palabrasReservadas = new HashMap<>();
        palabrasReservadas.put("and", TipoToken.AND);
        palabrasReservadas.put("else", TipoToken.ELSE);
        palabrasReservadas.put("false", TipoToken.FALSE);
        palabrasReservadas.put("for", TipoToken.FOR);
        palabrasReservadas.put("fun", TipoToken.FUN);
        palabrasReservadas.put("if", TipoToken.IF);
        palabrasReservadas.put("null", TipoToken.NULL);
        palabrasReservadas.put("or", TipoToken.OR);
        palabrasReservadas.put("print", TipoToken.PRINT);
        palabrasReservadas.put("return", TipoToken.RETURN);
        palabrasReservadas.put("true", TipoToken.TRUE);
        palabrasReservadas.put("var", TipoToken.VAR);
        palabrasReservadas.put("while", TipoToken.WHILE);

        // Para no tener que incluirlo en el switch-case se coloca aqui para ahorrar
        // codigo--------
        palabrasReservadas.put("(", TipoToken.LEFT_PAREN);
        palabrasReservadas.put(")", TipoToken.RIGHT_PAREN);
        palabrasReservadas.put("{", TipoToken.LEFT_BRACE);
        palabrasReservadas.put("}", TipoToken.RIGHT_BRACE);
        palabrasReservadas.put(",", TipoToken.COMMA);
        palabrasReservadas.put(".", TipoToken.DOT);
        palabrasReservadas.put("-", TipoToken.MINUS);
        palabrasReservadas.put("+", TipoToken.PLUS);
        palabrasReservadas.put(";", TipoToken.SEMICOLON);
        palabrasReservadas.put("*", TipoToken.STAR);
    }

    private final String source;

    private final List<Token> tokens = new ArrayList<>();

    public Scanner(String source) {
        this.source = source + " ";
    }

    public List<Token> scan() throws Exception {
        String lexema = "";
        String numero = ""; // Var numero para convertir texto a un numero en doble.
        String exponente = "";
        String signo = "";
        int estado = 0;
        char c;

        for (int i = 0; i < source.length(); i++) {
            c = source.charAt(i);

            switch (estado) {

                case 0:
                    if (Character.isLetter(c)) {
                        estado = 8;
                        lexema += c;
                    } else if (Character.isDigit(c)) {
                        estado = 9;
                        lexema += c;
                    }

                    /*
                     * A partir de este punto, se introducen los caracteres que se están
                     * leyendo. Es posible que algunos de estos caracteres se combinen con
                     * otros, por lo que se pasan a otro estado para analizar todas las
                     * posibilidades y determinar qué tipo de token se debe crear.---------
                     */

                    else if (c == '<') {
                        estado = 1;
                        lexema += c;
                    } else if (c == '=') {
                        estado = 2;
                        lexema += c;
                    } else if (c == '>') {
                        estado = 3;
                        lexema += c;
                    } else if (c == '!') {
                        estado = 4;
                        lexema += c;
                    } else if (c == '/') {
                        estado = 5;
                        lexema += c;
                    } else if (c == '"') {
                        estado = 6;
                        lexema += c;
                    }

                    /*
                     * Este caso se trata de generar tokens a partir de un solo carácter.
                     * Cualquier carácter que no esté contemplado o no sea relevante será descartado
                     */

                    else {
                        estado = 7;
                        lexema += c;
                    }
                    break;

                case 1:
                    if (c == '=') {

                        estado = 1;
                        lexema += c;

                        Token t = new Token(TipoToken.LESS_EQUAL, lexema);
                        tokens.add(t);

                        estado = 0;// Después de agregar un token, el proceso vuelve al estado de lectura inicial
                        lexema = "";// La cadena se vacía después de analizar el nuevo token.
                    } else {
                        Token t = new Token(TipoToken.LESS, lexema);
                        tokens.add(t);

                        /*
                         * Cada vez que se encuentra un 'i--', significa que se ha leído un carácter
                         * adicional
                         * que no forma parte del token generado,y esto provoca que la posición de
                         * lectura retroceda una posición.
                         */
                        i--;
                        lexema = "";
                        estado = 0;
                    }
                    break;
                // El mismo proceso se repite hasta llegar al estado 5.
                case 2:
                    if (c == '=') {

                        estado = 2;
                        lexema += c;

                        Token t = new Token(TipoToken.EQUAL_EQUAL, lexema);
                        tokens.add(t);

                        estado = 0;
                        lexema = "";
                    } else {
                        Token t = new Token(TipoToken.EQUAL, lexema);
                        tokens.add(t);

                        i--;
                        lexema = "";
                        estado = 0;
                    }
                    break;

                case 3:
                    if (c == '=') {

                        estado = 3;
                        lexema += c;

                        Token t = new Token(TipoToken.GREATER_EQUAL, lexema);
                        tokens.add(t);

                        estado = 0;
                        lexema = "";
                    } else {
                        Token t = new Token(TipoToken.GREATER, lexema);
                        tokens.add(t);

                        i--;
                        lexema = "";
                        estado = 0;
                    }
                    break;

                case 4:
                    if (c == '=') {

                        estado = 4;
                        lexema += c;

                        Token t = new Token(TipoToken.BANG_EQUAL, lexema);
                        tokens.add(t);

                        estado = 0;
                        lexema = "";
                    } else {
                        Token t = new Token(TipoToken.BANG, lexema);
                        tokens.add(t);

                        i--;
                        lexema = "";
                        estado = 0;
                    }
                    break;

                // Estado para comentarios
                case 5:
                    // Comentarios de linea
                    if (c == '/') {

                        estado = 5;
                        lexema += c;

                        /*
                         * si se quiere guardar un token de comentario:
                         * Token t = new Token(TipoToken.LINE_COMMENT, lexema);
                         * tokens.add(t);
                         * lexema = "";
                         */

                        /*
                         * Esto está registrando el contenido del comentario hasta
                         * encontrar un salto de línea o hasta alcanzar el final del archivo.
                         */
                        i++;
                        c = source.charAt(i);
                        while (c != (char) 10 && i < source.length() - 1) {
                            lexema += c;
                            i++;
                            c = source.charAt(i);
                        }

                        /*
                         * Token t1 = new Token(TipoToken.COMMENT, lexema);
                         * tokens.add(t1);
                         */
                        lexema = "";
                        estado = 0;
                    }

                    // Comentario Multilinea
                    else if (c == '*') {
                        estado = 5;
                        lexema += c;

                        /*
                         * Token t = new Token(TipoToken.MULTILINE_COMMENT, lexema);
                         * tokens.add(t);
                         * lexema = "";
                         */

                        i++;
                        c = source.charAt(i);

                        /*
                         * Se utiliza un bucle 'do while' porque este bucle continúa
                         * ejecutándose hasta que se encuentra la secuencia '/'. En contraste,
                         * un bucle 'while' estándar haría que la cadena '//' abriera y cerrara el
                         * comentario.
                         */
                        do {
                            lexema += c;
                            i++;
                            c = source.charAt(i);
                        } while (source.charAt(i - 1) != '*' && source.charAt(i) != '/' && i < source.length() - 1);// <<Hasta
                                                                                                                    // eu
                                                                                                                    // se
                                                                                                                    // ecnuentre
                                                                                                                    // "*/"
                                                                                                                    // o
                                                                                                                    // termine
                                                                                                                    // el
                                                                                                                    // archivo
                        /*
                         * Token t2 = new Token(TipoToken.COMMENT, lexema.substring(0,lexema.length()-1)
                         * );
                         * tokens.add(t2);
                         */
                        lexema = "";
                        estado = 0;

                    } else {
                        Token t = new Token(TipoToken.SLASH, lexema);
                        tokens.add(t);

                        i--;
                        lexema = "";
                        estado = 0;
                    }

                    break;

                // Caso para las cadenas que deben comenzar con comillas genera un token
                // inválido si no se cierran adecuadamente.
                case 6:
                    estado = 6;

                    i++;
                    lexema += c;
                    c = source.charAt(i);

                    // Hasta que se encuentre un salto de línea,(") o se llegue al final del
                    // archivo.
                    while (c != (char) 10 && c != '"' && i < source.length() - 1) {
                        lexema += c;
                        i++;
                        c = source.charAt(i);
                    }

                    // Para determinar en qué caso se terminó el bucle.
                    if (source.charAt(i) == '"') {
                        lexema += c;
                        Token t = new Token(TipoToken.STRING, lexema, lexema.substring(1, lexema.length() - 1));
                        tokens.add(t);
                        lexema = "";
                        estado = 0;

                    } else {
                        /*
                         * Si no es necesario utilizar un token inválido,
                         * puedes comentar las siguientes dos líneas. Esto hará que
                         * las cadenas que no se cierran con comillas dobles (") desaparezcan del
                         * análisis
                         * Token t = new Token(TipoToken.INVALID, lexema);
                         * tokens.add(t);
                         */

                        estado = 0;
                        lexema = "";
                    }
                    break;

                /*
                 * Este es el caso para crear tokens a partir de un carácter,
                 * donde se descartan los caracteres que no están previamente definidos.
                 */
                case 7:
                    TipoToken tt11 = palabrasReservadas.get(lexema);
                    estado = 7;

                    if (tt11 != null) {
                        Token t11 = new Token(tt11, lexema);
                        tokens.add(t11);
                    }

                    lexema = "";
                    i--;
                    estado = 0;

                    break;

                // Caso utilizado para generar el token de identificadores.
                case 8:
                    if (Character.isLetter(c) || Character.isDigit(c)) {
                        estado = 8;
                        lexema += c;
                    } else {
                        TipoToken tt1 = palabrasReservadas.get(lexema);

                        if (tt1 == null) {
                            Token t4 = new Token(TipoToken.IDENTIFIER, lexema);
                            tokens.add(t4);
                        } else {
                            Token t5 = new Token(tt1, lexema);
                            tokens.add(t5);
                        }

                        i--;
                        lexema = "";
                        estado = 0;
                    }
                    break;

                // Caso utilizado para generar el token de números
                case 9:
                    /*
                     * Esto consiste en almacenar el número a medida que se sale del caso
                     * y luego reingresa carácter por carácter, con el propósito de mantener
                     * el formato requerido por el profesor.
                     */

                    if (Character.isDigit(c)) {
                        estado = 9;
                        lexema += c;
                    }

                    /*
                     * En este punto, se asume que se ha completado la parte entera
                     * y se comienza a leer la parte decimal del número.
                     */
                    else if (c == '.') {
                        lexema += c;

                        i++;
                        c = source.charAt(i);
                        // En esta sección se lee y procesa la totalidad de la parte decimal del número.
                        while (i < source.length() - 1 && Character.isDigit(c)) {
                            lexema += c;
                            i++;
                            c = source.charAt(i);
                        }

                        // El número se almacena como una cadena en formato flotante
                        numero = lexema;
                        // Comienza la sección relacionada con la parte exponencial.
                        if (source.charAt(i) == 'E') {
                            lexema += c;
                            i++;
                            c = source.charAt(i);
                            // "Determina si el exponente tiene signo o no
                            if (Character.isDigit(c)) {
                                lexema += c;
                                exponente += c;
                                i++;
                                c = source.charAt(i);
                                // Continúa mientras se lea un dígito y no se alcance el final del archivo
                                while (i < source.length() - 1 && Character.isDigit(c)) {
                                    lexema += c;
                                    exponente += c;
                                    i++;
                                    c = source.charAt(i);
                                }
                            } else if (c == '+' || c == '-') {
                                lexema += c;
                                signo += c;
                                i++;
                                c = source.charAt(i);
                                while (i < source.length() - 1 && Character.isDigit(c)) {
                                    lexema += c;
                                    exponente += c;
                                    i++;
                                    c = source.charAt(i);
                                }
                            }

                            /*
                             * Genera el token de número y convierte el texto en un valor numérico.
                             * Se enfrenta a problemas al manejar exponentes grandes.
                             */
                            Token t69 = new Token(TipoToken.NUMBER, lexema,
                                    Float.parseFloat(numero) * Math.pow(10, Float.parseFloat(signo + exponente)));
                            tokens.add(t69);
                            estado = 0;
                            exponente = "";
                            numero = "";
                            lexema = "";
                            signo = "";
                            i--;
                        }
                        // Caso para procesar números con punto decimal sin exponente.
                        else {
                            // Se añade el '0' para tratar los casos en los que se recibe 'número.' con un
                            // punto decimal
                            Token t6 = new Token(TipoToken.NUMBER, lexema, Float.parseFloat(lexema + "0"));
                            tokens.add(t6);
                            estado = 0;
                            exponente = "";
                            numero = "";
                            lexema = "";
                            signo = "";
                            i--;
                        }
                    }
                    //// caso para manejar numero entero con exponente
                    else if (c == 'E') {
                        numero = lexema;
                        lexema += c;
                        i++;
                        c = source.charAt(i);
                        if (Character.isDigit(c)) {
                            lexema += c;
                            exponente += c;
                            i++;
                            c = source.charAt(i);
                            while (i < source.length() - 1 && Character.isDigit(c)) {
                                lexema += c;
                                exponente += c;
                                i++;
                                c = source.charAt(i);
                            }
                        } else if (c == '+' || c == '-') {
                            lexema += c;
                            signo += c;
                            i++;
                            c = source.charAt(i);
                            while (i < source.length() - 1 && Character.isDigit(c)) {
                                lexema += c;
                                exponente += c;
                                i++;
                                c = source.charAt(i);
                            }
                        }
                        Token t69 = new Token(TipoToken.NUMBER, lexema,
                                Integer.valueOf(numero) * Math.pow(10, Float.parseFloat(signo + exponente)));
                        tokens.add(t69);
                        estado = 0;
                        exponente = "";
                        numero = "";
                        lexema = "";
                        signo = "";
                        i--;
                    }
                    // Caso para procesar números enteros sin exponente
                    else {
                        Token t7 = new Token(TipoToken.NUMBER, lexema, Integer.valueOf(lexema));
                        tokens.add(t7);

                        estado = 0;
                        exponente = "";
                        numero = "";
                        lexema = "";
                        signo = "";
                        i--;
                    }
                    break;
            }
        }
        return tokens;
    }
}
