import { withBase } from 'vitepress'
import { borderColor } from './experienceskills'

/**
 * Renders the Experience Bottle the way the game does: layer0 as is, layer1 multiplied by the skill
 * colour and layer2 by its border colour. Done in the browser so any colour, including ones from config
 * files, gets an accurate icon without uploading a texture per skill.
 */
const LAYERS = ['experience_bottle', 'experience_bottle_layer1', 'experience_bottle_layer2']
const SCALE = 4

let layers: Promise<HTMLImageElement[]> | null = null
const rendered = new Map<number, Promise<string>>()

function load(src: string): Promise<HTMLImageElement> {
  return new Promise((resolve, reject) => {
    const image = new Image()
    image.onload = () => resolve(image)
    image.onerror = reject
    image.src = src
  })
}

function tinted(image: HTMLImageElement, rgb: number | null): HTMLCanvasElement {
  const canvas = document.createElement('canvas')
  canvas.width = image.naturalWidth
  canvas.height = image.naturalHeight
  const context = canvas.getContext('2d')!
  context.drawImage(image, 0, 0)
  if (rgb === null) return canvas
  const pixels = context.getImageData(0, 0, canvas.width, canvas.height)
  const [r, g, b] = [(rgb >> 16) & 0xff, (rgb >> 8) & 0xff, rgb & 0xff]
  for (let i = 0; i < pixels.data.length; i += 4) {
    pixels.data[i] = (pixels.data[i] * r) / 255
    pixels.data[i + 1] = (pixels.data[i + 1] * g) / 255
    pixels.data[i + 2] = (pixels.data[i + 2] * b) / 255
  }
  context.putImageData(pixels, 0, 0)
  return canvas
}

/** A PNG data URL of the bottle tinted for the given packed RGB colour. */
export function bottleIcon(rgb: number): Promise<string> {
  const color = rgb & 0xffffff
  const cached = rendered.get(color)
  if (cached) return cached
  layers ??= Promise.all(LAYERS.map((name) => load(withBase(`/items/${name}.png`))))
  const result = layers.then(([base, fill, border]) => {
    const size = base.naturalWidth
    const canvas = document.createElement('canvas')
    canvas.width = size * SCALE
    canvas.height = size * SCALE
    const context = canvas.getContext('2d')!
    // Scaled up here so the glint mask on the page stays as crisp as the icon.
    context.imageSmoothingEnabled = false
    for (const layer of [tinted(base, null), tinted(fill, color), tinted(border, borderColor(color))]) {
      context.drawImage(layer, 0, 0, canvas.width, canvas.height)
    }
    return canvas.toDataURL('image/png')
  })
  rendered.set(color, result)
  return result
}
