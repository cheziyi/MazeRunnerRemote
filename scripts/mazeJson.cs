using System;
namespace ScriptNs
{
    public class ScriptContainer
    {
        // This will send the arena information when there is an add or remove of obstacle, and send the robot position when its position and direction change
        // Information is in JSON format.

        // {"robotPosition": [x,y,direction]} 
        // where x and y are x and y positions with origin at the top left increasing towards bottom right, 
        // direction is the angle facing north at 0, increasing clockwise

        // {"grid": "hex"} where hex will be the hexidecimal string representing the arena
        // where 0 is a grid with no obstacle and 1 is a grid with obstacle, 
        // starting from the top left as the leftmost bit, increasing towards right, followed by the next row, until the rightmost bit will be the bottom right grid of the arena
        // if the number of grids of the arena is not divisible by 4, the rightmost bit will be padded by 0s.
        // You probably would not have to worry unless the cz/ce 3004 changes the arena size, which for a long time maintained the 20 x 15 grids. 20 * 15 = 300, 300 % 4 = 0 ;)

        public static string MainScript(int[,] gridLayout, int[] robotPosition, bool posTgridF, bool addObstacle, int[] obstaclePosition)
        {
            string stringToSend = "";
            if (posTgridF)
            {
                stringToSend = @"{""robotPos"" : [" + robotPosition[0] + ", " + robotPosition[1] + ", " + robotPosition[2] + "]}";
            }
            else
            {
                var part1 = "";

                for (int i = 0; i < 38; i++)
                {
                    part1 += "FF";
                }

                var width = gridLayout.GetLength(0);
                var height = gridLayout.GetLength(1);
                var bitNumber = 0;
                var part2 = "";
                var binaryString = "";
                for (var h = height - 1; h >= 0; h--)
                {
                    for (var w = 0; w < width; w++)
                    {
                        bitNumber++;
                        binaryString += gridLayout[w, h];
                        if (bitNumber % 8 == 0)
                        {
                            part2 += Convert.ToInt32(binaryString, 2).ToString("X2");
                            binaryString = "";
                            bitNumber = 0;
                        }
                    }
                }
                if (!binaryString.Equals(""))
                {
                    for (var i = bitNumber; i < 8; i++)
                        binaryString += 0;
                    part2 += Convert.ToInt32(binaryString, 2).ToString("X2");
                }

                stringToSend = "{\"gridP1\":\"" + part1 + "\", \"gridP2\":\"" + part2 + "\"}";
            }

            return stringToSend + "\r\n";
        }


    }

}