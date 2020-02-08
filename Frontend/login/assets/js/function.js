String.prototype.format = function (...args) {
  if (!this.match(/^(?:(?:(?:[^{}]|(?:\{\{)|(?:\}\}))+)|(?:\{[0-9]+\}))+$/)) {
    throw new Error('invalid format string.')
  }
  return this.replace(/((?:[^{}]|(?:\{\{)|(?:\}\}))+)|(?:\{([0-9]+)\})/g, (m, str, index) => {
    if (str) {
      return str.replace(/(?:{{)|(?:}})/g, m => m[0])
    } else {
      if (index >= args.length) {
        throw new Error('argument index is out of range in format')
      }
      return args[index]
    }
  })
}

class asyncFunction {
  constructor (fun, callback, ...args) {
    this.fun = fun
    this.args = args
    this.callback = callback
  }

  run () {
    if (this.callback !== null) {
      this.callback(this.fun(this.args))
    } else {
      return this.fun(this.args)
    }
  }
}

async function asyncRun (callback, ...functions) {
  const arr = functions, textPromises = arr.map(async function (doc) {
    return await doc.run()
  })
  for (const textPromise of textPromises) {
    if (callback !== null) {
      callback(await textPromise)
    } else {
      console.log(await textPromise)
    }
  }
}

$(()=>{
  //asyncRun(null,new asyncFunction(()=>{console.log(1)},null),new asyncFunction(()=>{console.log(2)},null),new asyncFunction(()=>{console.log(3)},null))
})